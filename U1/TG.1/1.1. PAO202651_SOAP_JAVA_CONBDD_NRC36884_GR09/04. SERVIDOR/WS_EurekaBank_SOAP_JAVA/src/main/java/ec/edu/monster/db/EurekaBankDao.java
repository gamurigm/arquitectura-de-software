package ec.edu.monster.db;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.CuentaDto;
import ec.edu.monster.modelo.SolicitudTransferencia;
import ec.edu.monster.seguridad.PasswordHasher;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EurekaBankDao {
    private static final String CODIGO_TIPO_DEPOSITO = "003";

    public boolean autenticar(String usuario, char[] contrasena) throws SQLException {
        if (contrasena == null) {
            return false;
        }

        try {
            String hashGuardado;
            try (Connection cn = ConexionDB.getInstancia().getConnection();
                    PreparedStatement ps = cn.prepareStatement(
                            "SELECT contrasena FROM Credenciales WHERE usuario = ?")) {
                ps.setString(1, usuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    hashGuardado = rs.getString("contrasena");
                }
            }
            return PasswordHasher.verificar(contrasena, hashGuardado);
        } finally {
            Arrays.fill(contrasena, '\0');
        }
    }

    public String obtenerCodigoCliente(String usuario) throws SQLException {
        try (Connection cn = ConexionDB.getInstancia().getConnection();
                PreparedStatement ps = cn.prepareStatement("SELECT chr_cliecodigo FROM Credenciales WHERE usuario = ?")) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getString(1) != null ? rs.getString(1).strip() : null;
            }
        }
    }

    public List<CuentaDto> traerCuentas(String codigoCliente) throws SQLException {
        List<CuentaDto> cuentas = new ArrayList<>();
        String sql = "SELECT chr_cuencodigo, chr_monecodigo, dec_cuensaldo FROM Cuenta "
                + "WHERE chr_cliecodigo = ? AND vch_cuenestado = 'ACTIVO' ORDER BY chr_cuencodigo";
        try (Connection cn = ConexionDB.getInstancia().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cuentas.add(new CuentaDto(rs.getString(1).strip(), rs.getString(2).strip(), rs.getBigDecimal(3)));
            }
        }
        return cuentas;
    }

    public List<Movimiento> traerMovimientos(String cuenta, String codigoCliente) throws SQLException {
        String sql = "SELECT m.chr_cuencodigo AS cuenta, "
                + "m.int_movinumero AS nromov, m.dtt_movifecha AS fecha, "
                + "t.vch_tipodescripcion AS tipo, t.vch_tipoaccion AS accion, "
                + "m.dec_moviimporte AS importe "
                + "FROM Movimiento m "
                + "INNER JOIN TipoMovimiento t ON t.chr_tipocodigo = m.chr_tipocodigo "
                + "INNER JOIN Cuenta c ON c.chr_cuencodigo = m.chr_cuencodigo "
                + "WHERE m.chr_cuencodigo = ? AND c.chr_cliecodigo = ? AND c.vch_cuenestado = 'ACTIVO' "
                + "ORDER BY m.int_movinumero DESC";
        List<Movimiento> movimientos = new ArrayList<>();

        try (Connection cn = ConexionDB.getInstancia().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cuenta);
            ps.setString(2, codigoCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimiento movimiento = new Movimiento();
                    movimiento.setCuenta(rs.getString("cuenta"));
                    movimiento.setNromov(rs.getInt("nromov"));
                    movimiento.setFecha(rs.getDate("fecha"));
                    movimiento.setTipo(rs.getString("tipo"));
                    movimiento.setAccion(rs.getString("accion"));
                    movimiento.setImporte(rs.getBigDecimal("importe"));
                    movimientos.add(movimiento);
                }
            }
        }

        return movimientos;
    }

    public void registrarDeposito(String cuenta, BigDecimal importe, String codigoEmpleado, String codigoCliente)
            throws SQLException {
        try (Connection cn = ConexionDB.getInstancia().getConnection()) {
            cn.setAutoCommit(false);
            try {
                BigDecimal saldo;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT dec_cuensaldo FROM Cuenta "
                        + "WHERE chr_cuencodigo = ? AND chr_cliecodigo = ? AND vch_cuenestado = 'ACTIVO' "
                        + "FOR UPDATE")) {
                    ps.setString(1, cuenta);
                    ps.setString(2, codigoCliente);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("La cuenta no existe o no está activa.");
                        }
                        saldo = rs.getBigDecimal("dec_cuensaldo");
                    }
                }

                int siguienteMovimiento;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT COALESCE(MAX(int_movinumero), 0) + 1 AS siguiente "
                        + "FROM Movimiento WHERE chr_cuencodigo = ?")) {
                    ps.setString(1, cuenta);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        siguienteMovimiento = rs.getInt("siguiente");
                    }
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE Cuenta SET dec_cuensaldo = ?, "
                        + "int_cuencontmov = int_cuencontmov + 1 "
                        + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO'")) {
                    ps.setBigDecimal(1, saldo.add(importe));
                    ps.setString(2, cuenta);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("No se pudo actualizar el saldo de la cuenta.");
                    }
                }

                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO Movimiento "
                        + "(chr_cuencodigo, int_movinumero, dtt_movifecha, "
                        + "chr_emplcodigo, chr_tipocodigo, dec_moviimporte) "
                        + "VALUES (?, ?, CURRENT_DATE, ?, ?, ?)")) {
                    ps.setString(1, cuenta);
                    ps.setInt(2, siguienteMovimiento);
                    ps.setString(3, codigoEmpleado);
                    ps.setString(4, CODIGO_TIPO_DEPOSITO);
                    ps.setBigDecimal(5, importe);
                    ps.executeUpdate();
                }

                cn.commit();
            } catch (SQLException | RuntimeException e) {
                try {
                    cn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }
                throw e;
            }
        }
    }

    public boolean transferir(String usuario, String codigoCliente, SolicitudTransferencia solicitud) throws SQLException {
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
                    ps.setString(1, id); ps.setString(2, usuario); ps.setString(3, solicitud.getClaveOperacion());
                    ps.setString(4, codigoCliente); ps.setString(5, solicitud.getCuentaOrigen());
                    ps.setString(6, solicitud.getCuentaDestino()); ps.setBigDecimal(7, solicitud.getImporte());
                    ps.executeUpdate();
                }
                actualizarSaldo(cn, solicitud.getCuentaOrigen(), solicitud.getImporte().negate());
                actualizarSaldo(cn, solicitud.getCuentaDestino(), solicitud.getImporte());
                insertarMovimientoTransferencia(cn, solicitud.getCuentaOrigen(), siguienteMovimiento(cn, solicitud.getCuentaOrigen()),
                        solicitud.getCuentaDestino(), "009", solicitud.getImporte(), id);
                insertarMovimientoTransferencia(cn, solicitud.getCuentaDestino(), siguienteMovimiento(cn, solicitud.getCuentaDestino()),
                        solicitud.getCuentaOrigen(), "008", solicitud.getImporte(), id);
                cn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                try { cn.rollback(); } catch (SQLException rollback) { e.addSuppressed(rollback); }
                if (esClaveDuplicada(e)) return resultadoIdempotente(usuario, solicitud) == Boolean.TRUE;
                throw e;
            }
        }
    }

    private CuentaTransferencia bloquearCuenta(Connection cn, String cuenta) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("SELECT chr_cliecodigo, chr_monecodigo, dec_cuensaldo FROM Cuenta "
                + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO' FOR UPDATE")) {
            ps.setString(1, cuenta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new CuentaTransferencia(rs.getString(1).strip(), rs.getString(2).strip(), rs.getBigDecimal(3)) : null;
            }
        }
    }

    private Boolean resultadoIdempotente(Connection cn, String usuario, SolicitudTransferencia solicitud) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("SELECT chr_cuencodigo_origen, chr_cuencodigo_destino, dec_importe "
                + "FROM Transferencias WHERE usuario = ? AND clave_operacion = ?")) {
            ps.setString(1, usuario); ps.setString(2, solicitud.getClaveOperacion());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString(1).strip().equals(solicitud.getCuentaOrigen())
                        && rs.getString(2).strip().equals(solicitud.getCuentaDestino())
                        && rs.getBigDecimal(3).compareTo(solicitud.getImporte()) == 0;
            }
        }
    }

    private Boolean resultadoIdempotente(String usuario, SolicitudTransferencia solicitud) throws SQLException {
        try (Connection cn = ConexionDB.getInstancia().getConnection()) { return resultadoIdempotente(cn, usuario, solicitud); }
    }

    private static boolean esClaveDuplicada(Throwable error) {
        for (Throwable actual = error; actual != null; actual = actual.getCause()) {
            if (actual instanceof SQLException sql) {
                if (sql.getErrorCode() == 1062) return true;
                for (SQLException siguiente = sql.getNextException(); siguiente != null; siguiente = siguiente.getNextException())
                    if (siguiente.getErrorCode() == 1062) return true;
            }
        }
        return false;
    }

    private void actualizarSaldo(Connection cn, String cuenta, BigDecimal delta) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("UPDATE Cuenta SET dec_cuensaldo = dec_cuensaldo + ?, "
                + "int_cuencontmov = int_cuencontmov + 1 WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO'")) {
            ps.setBigDecimal(1, delta); ps.setString(2, cuenta);
            if (ps.executeUpdate() != 1) throw new SQLException("No se pudo actualizar el saldo de la cuenta.");
        }
    }

    private int siguienteMovimiento(Connection cn, String cuenta) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("SELECT COALESCE(MAX(int_movinumero), 0) + 1 FROM Movimiento WHERE chr_cuencodigo = ?")) {
            ps.setString(1, cuenta);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    private void insertarMovimientoTransferencia(Connection cn, String cuenta, int numero, String referencia,
            String tipo, BigDecimal importe, String id) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("INSERT INTO Movimiento (chr_cuencodigo, int_movinumero, "
                + "dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia, id_transferencia) "
                + "VALUES (?, ?, CURRENT_DATE, '0001', ?, ?, ?, ?)")) {
            ps.setString(1, cuenta); ps.setInt(2, numero); ps.setString(3, tipo); ps.setBigDecimal(4, importe);
            ps.setString(5, referencia); ps.setString(6, id); ps.executeUpdate();
        }
    }

    private record CuentaTransferencia(String codigoCliente, String moneda, BigDecimal saldo) { }
}
