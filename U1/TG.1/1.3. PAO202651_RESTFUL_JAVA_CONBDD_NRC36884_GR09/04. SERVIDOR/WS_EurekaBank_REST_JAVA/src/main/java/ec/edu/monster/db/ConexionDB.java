package ec.edu.monster.db;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public final class ConexionDB {
    private static final ConexionDB INSTANCIA = new ConexionDB();
    private final DataSource fuente;

    private ConexionDB() {
        MysqlDataSource mysql = new MysqlDataSource();
        mysql.setURL(System.getenv().getOrDefault("EUREKABANK_DB_URL",
                "jdbc:mysql://127.0.0.1:3306/eurekabank_gr09?sslMode=DISABLED&allowPublicKeyRetrieval=true"));
        fuente = mysql;
    }

    public static ConexionDB getInstancia() {
        return INSTANCIA;
    }

    public Connection getConnection() throws SQLException {
        return fuente.getConnection(requerida("EUREKABANK_DB_USER"), requerida("EUREKABANK_DB_PASSWORD"));
    }

    private static String requerida(String variable) throws SQLException {
        String valor = System.getenv(variable);
        if (valor == null || valor.isBlank()) {
            throw new SQLException("Falta configurar la variable " + variable + ".");
        }
        return valor;
    }
}
