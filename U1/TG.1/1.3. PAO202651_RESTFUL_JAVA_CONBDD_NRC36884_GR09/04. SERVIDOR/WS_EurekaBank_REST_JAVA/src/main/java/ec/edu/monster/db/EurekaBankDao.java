package ec.edu.monster.db;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.SolicitudTransferencia;
import ec.edu.monster.seguridad.PasswordHasher;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class EurekaBankDao implements RepositorioEurekaBank {
    private static final String TIPO_DEPOSITO = "003";
    private static final String EMPLEADO_DEPOSITO = "0001";

    @Override public boolean autenticar(String usuario, char[] contrasena) throws SQLException {
        if (contrasena == null) return false;
        try {
            String hash;
            try (Connection cn = ConexionDB.getInstancia().getConnection();
                    PreparedStatement ps = cn.prepareStatement("SELECT contrasena FROM Credenciales WHERE usuario = ?")) {
                ps.setString(1, usuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return false;
                    hash = rs.getString("contrasena");
                }
            }
            return PasswordHasher.verificar(contrasena, hash);
        } finally {
            Arrays.fill(contrasena, '\0');
        }
    }

    @Override public String obtenerCodigoCliente(String usuario) throws SQLException {
        try (Connection cn = ConexionDB.getInstancia().getConnection();
                PreparedStatement ps = cn.prepareStatement("SELECT chr_cliecodigo FROM Credenciales WHERE usuario = ?")) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getString(1) != null ? rs.getString(1).strip() : null;
            }
        }
    }

    @Override public List<Movimiento> traerMovimientos(String cuenta) throws SQLException {
        String sql = "SELECT m.int_movinumero numero, m.chr_cuencodigo cuenta, m.dtt_movifecha fecha, "
                + "t.vch_tipodescripcion tipo, t.vch_tipoaccion accion, m.dec_moviimporte importe "
                + "FROM Movimiento m JOIN TipoMovimiento t ON t.chr_tipocodigo = m.chr_tipocodigo "
                + "WHERE m.chr_cuencodigo = ? ORDER BY m.int_movinumero DESC";
        List<Movimiento> movimientos = new ArrayList<>();
        try (Connection cn = ConexionDB.getInstancia().getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cuenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimiento movimiento = new Movimiento();
                    movimiento.setNumero(rs.getInt("numero"));
                    movimiento.setCuenta(rs.getString("cuenta"));
                    movimiento.setFecha(rs.getDate("fecha").toLocalDate());
                    movimiento.setTipo(rs.getString("tipo"));
                    movimiento.setAccion(rs.getString("accion"));
                    movimiento.setImporte(rs.getBigDecimal("importe"));
                    movimientos.add(movimiento);
                }
            }
        }
        return movimientos;
    }

    @Override public boolean registrarDeposito(String cuenta, BigDecimal importe) throws SQLException {
        try (Connection cn = ConexionDB.getInstancia().getConnection()) {
            cn.setAutoCommit(false);
            try {
                BigDecimal saldo;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT dec_cuensaldo FROM Cuenta WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO' FOR UPDATE")) {
                    ps.setString(1, cuenta);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return false;
                        }
                        saldo = rs.getBigDecimal("dec_cuensaldo");
                    }
                }
                int numero;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT COALESCE(MAX(int_movinumero), 0) + 1 FROM Movimiento WHERE chr_cuencodigo = ?")) {
                    ps.setString(1, cuenta);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        numero = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE Cuenta SET dec_cuensaldo = ?, int_cuencontmov = int_cuencontmov + 1 "
                                + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO'")) {
                    ps.setBigDecimal(1, saldo.add(importe));
                    ps.setString(2, cuenta);
                    if (ps.executeUpdate() != 1) throw new SQLException("No se pudo actualizar la cuenta.");
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO Movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte) "
                                + "VALUES (?, ?, CURRENT_DATE, ?, ?, ?)")) {
                    ps.setString(1, cuenta);
                    ps.setInt(2, numero);
                    ps.setString(3, EMPLEADO_DEPOSITO);
                    ps.setString(4, TIPO_DEPOSITO);
                    ps.setBigDecimal(5, importe);
                    ps.executeUpdate();
                }
                cn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                try { cn.rollback(); } catch (SQLException rollbackError) { e.addSuppressed(rollbackError); }
                throw e;
            }
        }
    }

    @Override public boolean transferir(String usuario, String codigoCliente, SolicitudTransferencia solicitud) throws SQLException {
        String id = java.util.UUID.randomUUID().toString();
        try (Connection cn = ConexionDB.getInstancia().getConnection()) {
            cn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            cn.setAutoCommit(false);
            try {
                Boolean repetida = resultadoIdempotente(cn, usuario, solicitud);
                if (repetida != null) { cn.commit(); return repetida; }
                Map<String, CuentaTransferencia> cuentas = new HashMap<>();
                var codigos = java.util.stream.Stream.of(solicitud.getCuentaOrigen(), solicitud.getCuentaDestino())
                        .sorted(Comparator.naturalOrder()).toList();
                for (String codigo : codigos) {
                    CuentaTransferencia cuenta = bloquearCuenta(cn, codigo);
                    if (cuenta == null) { cn.rollback(); return false; }
                    cuentas.put(codigo, cuenta);
                }

                repetida = resultadoIdempotente(cn, usuario, solicitud);
                if (repetida != null) { cn.commit(); return repetida; }
                CuentaTransferencia origen = cuentas.get(solicitud.getCuentaOrigen());
                CuentaTransferencia destino = cuentas.get(solicitud.getCuentaDestino());
                if (!origen.codigoCliente().equals(codigoCliente) || !origen.moneda().equals(destino.moneda())
                        || origen.saldo().compareTo(solicitud.getImporte()) < 0) {
                    cn.rollback();
                    return false;
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO Transferencias (id_transferencia, usuario, clave_operacion, chr_cliecodigo, "
                                + "chr_cuencodigo_origen, chr_cuencodigo_destino, dec_importe, dtt_fecha) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)")) {
                    ps.setString(1, id);
                    ps.setString(2, usuario);
                    ps.setString(3, solicitud.getClaveOperacion());
                    ps.setString(4, codigoCliente);
                    ps.setString(5, solicitud.getCuentaOrigen());
                    ps.setString(6, solicitud.getCuentaDestino());
                    ps.setBigDecimal(7, solicitud.getImporte());
                    ps.executeUpdate();
                }

                actualizarSaldo(cn, solicitud.getCuentaOrigen(), solicitud.getImporte().negate());
                actualizarSaldo(cn, solicitud.getCuentaDestino(), solicitud.getImporte());
                int numeroOrigen = siguienteMovimiento(cn, solicitud.getCuentaOrigen());
                int numeroDestino = siguienteMovimiento(cn, solicitud.getCuentaDestino());
                insertarMovimientoTransferencia(cn, solicitud.getCuentaOrigen(), numeroOrigen,
                        solicitud.getCuentaDestino(), "009", solicitud.getImporte(), id);
                insertarMovimientoTransferencia(cn, solicitud.getCuentaDestino(), numeroDestino,
                        solicitud.getCuentaOrigen(), "008", solicitud.getImporte(), id);
                cn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                try { cn.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
                if (esClaveDuplicada(e)) return Boolean.TRUE.equals(resultadoIdempotente(usuario, solicitud));
                throw e;
            }
        }
    }

    private CuentaTransferencia bloquearCuenta(Connection cn, String cuenta) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT chr_cliecodigo, chr_monecodigo, dec_cuensaldo FROM Cuenta "
                        + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO' FOR UPDATE")) {
            ps.setString(1, cuenta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new CuentaTransferencia(rs.getString(1).strip(), rs.getString(2).strip(), rs.getBigDecimal(3)) : null;
            }
        }
    }

    private Boolean resultadoIdempotente(Connection cn, String usuario, SolicitudTransferencia solicitud) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT chr_cuencodigo_origen, chr_cuencodigo_destino, dec_importe FROM Transferencias "
                        + "WHERE usuario = ? AND clave_operacion = ?")) {
            ps.setString(1, usuario);
            ps.setString(2, solicitud.getClaveOperacion());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString(1).strip().equals(solicitud.getCuentaOrigen())
                        && rs.getString(2).strip().equals(solicitud.getCuentaDestino())
                        && rs.getBigDecimal(3).compareTo(solicitud.getImporte()) == 0;
            }
        }
    }

    private Boolean resultadoIdempotente(String usuario, SolicitudTransferencia solicitud) throws SQLException {
        try (Connection cn = ConexionDB.getInstancia().getConnection()) {
            return resultadoIdempotente(cn, usuario, solicitud);
        }
    }

    private static boolean esClaveDuplicada(Throwable e) {
        for (Throwable actual = e; actual != null; actual = actual.getCause()) {
            if (actual instanceof SQLException sql && sql.getErrorCode() == 1062) return true;
            if (actual instanceof SQLException sql) {
                for (SQLException siguiente = sql.getNextException(); siguiente != null; siguiente = siguiente.getNextException()) {
                    if (siguiente.getErrorCode() == 1062) return true;
                }
            }
        }
        return false;
    }

    private void actualizarSaldo(Connection cn, String cuenta, BigDecimal delta) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE Cuenta SET dec_cuensaldo = dec_cuensaldo + ?, int_cuencontmov = int_cuencontmov + 1 "
                        + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO'")) {
            ps.setBigDecimal(1, delta);
            ps.setString(2, cuenta);
            if (ps.executeUpdate() != 1) throw new SQLException("No se pudo actualizar el saldo de la cuenta.");
        }
    }

    private int siguienteMovimiento(Connection cn, String cuenta) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT COALESCE(MAX(int_movinumero), 0) + 1 FROM Movimiento WHERE chr_cuencodigo = ?")) {
            ps.setString(1, cuenta);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    private void insertarMovimientoTransferencia(Connection cn, String cuenta, int numero, String referencia,
            String tipo, BigDecimal importe, String id) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO Movimiento (chr_cuencodigo, int_movinumero, dtt_movifecha, chr_emplcodigo, "
                        + "chr_tipocodigo, dec_moviimporte, chr_cuenreferencia, id_transferencia) "
                        + "VALUES (?, ?, CURRENT_DATE, '0001', ?, ?, ?, ?)")) {
            ps.setString(1, cuenta);
            ps.setInt(2, numero);
            ps.setString(3, tipo);
            ps.setBigDecimal(4, importe);
            ps.setString(5, referencia);
            ps.setString(6, id);
            ps.executeUpdate();
        }
    }

    private record CuentaTransferencia(String codigoCliente, String moneda, BigDecimal saldo) { }
}
