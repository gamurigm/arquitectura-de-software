package ec.edu.monster.db;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.seguridad.PasswordHasher;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public List<Movimiento> traerMovimientos(String cuenta) throws SQLException {
        String sql = "SELECT m.chr_cuencodigo AS cuenta, "
                + "m.int_movinumero AS nromov, m.dtt_movifecha AS fecha, "
                + "t.vch_tipodescripcion AS tipo, t.vch_tipoaccion AS accion, "
                + "m.dec_moviimporte AS importe "
                + "FROM Movimiento m "
                + "INNER JOIN TipoMovimiento t ON t.chr_tipocodigo = m.chr_tipocodigo "
                + "WHERE m.chr_cuencodigo = ? "
                + "ORDER BY m.int_movinumero DESC";
        List<Movimiento> movimientos = new ArrayList<>();

        try (Connection cn = ConexionDB.getInstancia().getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cuenta);
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

    public void registrarDeposito(String cuenta, BigDecimal importe, String codigoEmpleado)
            throws SQLException {
        try (Connection cn = ConexionDB.getInstancia().getConnection()) {
            cn.setAutoCommit(false);
            try {
                BigDecimal saldo;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT dec_cuensaldo FROM Cuenta "
                        + "WHERE chr_cuencodigo = ? AND vch_cuenestado = 'ACTIVO' "
                        + "FOR UPDATE")) {
                    ps.setString(1, cuenta);
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
}
