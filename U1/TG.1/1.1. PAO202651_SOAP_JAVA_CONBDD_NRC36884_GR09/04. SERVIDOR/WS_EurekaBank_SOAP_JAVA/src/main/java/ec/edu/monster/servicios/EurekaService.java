package ec.edu.monster.servicios;

import ec.edu.monster.db.EurekaBankDao;
import ec.edu.monster.modelo.Movimiento;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public final class EurekaService {
    private static final String EMPLEADO_DEPOSITO = "0001";
    private final EurekaBankDao dao = new EurekaBankDao();

    public boolean iniciarSesion(String usuario, String contrasena) throws SQLException {
        if (usuario == null || usuario.isBlank() || usuario.length() > 30
                || contrasena == null || contrasena.isEmpty() || contrasena.length() > 1024) {
            return false;
        }
        return dao.autenticar(usuario.strip(), contrasena.toCharArray());
    }

    public List<Movimiento> traerMovimientos(String cuenta) throws SQLException {
        if (cuenta == null || cuenta.isBlank() || cuenta.strip().length() > 8) {
            return List.of();
        }
        return dao.traerMovimientos(cuenta.strip());
    }

    public int registrarDeposito(String cuenta, BigDecimal importe) throws SQLException {
        if (cuenta == null || cuenta.isBlank() || cuenta.strip().length() > 8 || importe == null) {
            return -1;
        }

        BigDecimal importeNormalizado;
        try {
            importeNormalizado = importe.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            return -1;
        }

        if (importeNormalizado.signum() <= 0 || importeNormalizado.precision() > 12) {
            return -1;
        }

        dao.registrarDeposito(cuenta.strip(), importeNormalizado, EMPLEADO_DEPOSITO);
        return 1;
    }
}
