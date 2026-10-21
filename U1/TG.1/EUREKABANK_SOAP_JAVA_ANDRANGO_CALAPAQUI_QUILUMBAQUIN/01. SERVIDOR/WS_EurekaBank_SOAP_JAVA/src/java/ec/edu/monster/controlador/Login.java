package ec.edu.monster.controlador;

import ec.edu.monster.db.AccesoDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author genes
 */
public class Login {
    public boolean IniciarSesion(String usuario, String contrasena) {
        Connection cn = null;
        boolean isValid = false;
        try {
            // Obtener la conexión
            cn = AccesoDB.getConnection();
            cn.setAutoCommit(false);

            // Hashear la contraseña con MD5
            String contrasenaHasheada = hashMD5(contrasena);

            // Consulta para validar usuario y contraseña
            String sql = "SELECT COUNT(*) AS conteo "
                    + "FROM Credenciales "
                    + "WHERE usuario = ? AND contrasena = ?";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, usuario);
            pstm.setString(2, contrasenaHasheada);
            
            // Ejecutar la consulta
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                int conteo = rs.getInt("conteo");
                isValid = (conteo > 0); // Si hay registros, el usuario es válido
            }
            
            // Cerrar recursos
            rs.close();
            pstm.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar usuario: " + e.getMessage());
        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al cerrar conexión: " + e.getMessage());
            }
        }
        return isValid;
    }

    // Método para hashear la contraseña con MD5
    private String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().toUpperCase(); // Retorna el hash MD5 como una cadena hexadecimal
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al realizar hash MD5", e);
        }
    }
}
