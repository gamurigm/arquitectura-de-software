package ec.edu.monster.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AccesoDB {
    private static final String URL = "jdbc:mysql://localhost:3306/eurekabank?useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "123456";

    public AccesoDB() {
    }
    
    public static Connection getConnection() throws SQLException {
        Connection cn = null;
        try {
            // Cargar el driver MySQL a memoria
            String driver = "com.mysql.cj.jdbc.Driver";
            Class.forName(driver);
            // Obtener el objeto Connection
            cn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new SQLException("Error al conectar a la base de datos: " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: No se encuentra el driver JDBC de MySQL.", e);
        }
        return cn;
    }
}
