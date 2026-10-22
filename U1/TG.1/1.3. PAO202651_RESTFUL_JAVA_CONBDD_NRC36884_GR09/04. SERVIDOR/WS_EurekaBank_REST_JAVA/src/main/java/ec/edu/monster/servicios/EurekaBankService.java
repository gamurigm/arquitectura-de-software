package ec.edu.monster.servicios;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.SolicitudTransferencia;
import ec.edu.monster.db.EurekaBankDao;
import ec.edu.monster.db.RepositorioEurekaBank;
import ec.edu.monster.seguridad.SesionesActivas;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public final class EurekaBankService {
    private final RepositorioEurekaBank dao;

    public EurekaBankService() { this(new EurekaBankDao()); }

    public EurekaBankService(RepositorioEurekaBank dao) { this.dao = dao; }

    public String iniciarSesion(String usuario, String contrasena) throws SQLException {
        if (usuario == null || usuario.isBlank() || usuario.strip().length() > 30
                || contrasena == null || contrasena.isEmpty() || contrasena.length() > 1024) return null;
        if (!dao.autenticar(usuario.strip(), contrasena.toCharArray())) return null;
        return SesionesActivas.getInstancia().crear(usuario.strip(), dao.obtenerCodigoCliente(usuario.strip()));
    }

    public List<Movimiento> traerMovimientos(String cuenta) throws SQLException {
        if (!cuentaValida(cuenta)) return List.of();
        return dao.traerMovimientos(cuenta.strip());
    }

    public boolean registrarDeposito(String cuenta, BigDecimal importe) throws SQLException {
        if (!cuentaValida(cuenta) || importe == null) return false;
        try {
            importe = importe.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            return false;
        }
        if (importe.signum() <= 0 || importe.precision() > 12) return false;
        return dao.registrarDeposito(cuenta.strip(), importe);
    }

    public Boolean transferir(String token, SolicitudTransferencia solicitud) throws SQLException {
        SesionesActivas.Sesion sesion = SesionesActivas.getInstancia().obtener(token);
        if (sesion == null) return null;
        if (sesion.codigoCliente() == null) return false;
        SolicitudTransferencia normalizada = TransferenciaValidator.normalizar(solicitud);
        if (normalizada == null) return false;
        return dao.transferir(sesion.usuario(), sesion.codigoCliente(), normalizada);
    }

    public void cerrarSesion(String token) { SesionesActivas.getInstancia().revocar(token); }

    private boolean cuentaValida(String cuenta) {
        return cuenta != null && !cuenta.isBlank() && cuenta.strip().length() == 8;
    }
}
