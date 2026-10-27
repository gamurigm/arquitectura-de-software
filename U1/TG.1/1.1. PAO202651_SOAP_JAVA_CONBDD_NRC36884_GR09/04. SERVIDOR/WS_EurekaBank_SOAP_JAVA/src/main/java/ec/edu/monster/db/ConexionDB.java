package ec.edu.monster.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {
    private static final ConexionDB INSTANCIA = new ConexionDB();
    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/eurekabank_gr09?sslMode=DISABLED&allowPublicKeyRetrieval=true";

    private ConexionDB() {
    }

    public static ConexionDB getInstancia() {
        return INSTANCIA;
    }

    public Connection getConnection() throws SQLException {
        String url = System.getenv("EUREKABANK_DB_URL");
        if (url == null || url.isBlank()) {
            url = System.getProperty("EUREKABANK_DB_URL", URL_POR_DEFECTO);
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el controlador JDBC de MySQL.", e);
        }

        return DriverManager.getConnection(
                url,
                requiredEnvironment("EUREKABANK_DB_USER"),
                requiredEnvironment("EUREKABANK_DB_PASSWORD"));
    }

    private static String requiredEnvironment(String name) throws SQLException {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = System.getProperty(name);
        }
        if (value == null || value.isBlank()) {
            throw new SQLException("Falta configurar la variable " + name + ".");
        }
        return value;
    }
}
