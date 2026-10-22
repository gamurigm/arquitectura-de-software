package ec.edu.monster.db;

import ec.edu.monster.modelo.Movimiento;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface RepositorioEurekaBank {
    boolean autenticar(String usuario, char[] contrasena) throws SQLException;
    String obtenerCodigoCliente(String usuario) throws SQLException;
    List<Movimiento> traerMovimientos(String cuenta) throws SQLException;
    boolean registrarDeposito(String cuenta, BigDecimal importe) throws SQLException;
    boolean transferir(String usuario, String codigoCliente, ec.edu.monster.modelo.SolicitudTransferencia solicitud) throws SQLException;
}
