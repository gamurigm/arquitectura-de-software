package ec.edu.monster.servicios;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.CuentaDto;
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
        String codigoCliente = dao.obtenerCodigoCliente(usuario.strip());
        return codigoCliente == null || codigoCliente.isBlank()
                ? null : SesionesActivas.getInstancia().crear(usuario.strip(), codigoCliente);
    }

    public List<CuentaDto> traerCuentas(String token) throws SQLException {
        SesionesActivas.Sesion sesion = SesionesActivas.getInstancia().obtener(token);
        if (sesion == null || sesion.codigoCliente() == null || sesion.codigoCliente().isBlank()) return null;
        return dao.traerCuentas(sesion.codigoCliente());
    }

    public List<Movimiento> traerMovimientos(String token, String cuenta) throws SQLException {
        SesionesActivas.Sesion sesion = SesionesActivas.getInstancia().obtener(token);
        if (sesion == null) return null;
        if (!cuentaValida(cuenta)) return List.of();
        if (sesion.codigoCliente() == null) return List.of();
        return dao.traerMovimientos(cuenta.strip(), sesion.codigoCliente());
    }

    public Boolean registrarDeposito(String token, String cuenta, BigDecimal importe) throws SQLException {
        SesionesActivas.Sesion sesion = SesionesActivas.getInstancia().obtener(token);
        if (sesion == null) return null;
        if (sesion.codigoCliente() == null) return false;
        if (!cuentaValida(cuenta) || importe == null) return false;
        try {
            importe = importe.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            return false;
        }
        if (importe.signum() <= 0 || importe.precision() > 12) return false;
        return dao.registrarDeposito(cuenta.strip(), importe, sesion.codigoCliente());
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
