package ec.edu.monster.ws;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.CuentaDto;
import ec.edu.monster.servicios.EurekaService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebService(serviceName = "WSEureka", targetNamespace = "http://ws.monster.edu.ec/")
public class WSEureka {
    private static final Logger LOGGER = Logger.getLogger(WSEureka.class.getName());
    private final EurekaService service = new EurekaService();

    @WebMethod(operationName = "login")
    @WebResult(name = "login")
    public int iniciarSesion(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasena") String contrasena) {
        try {
            return service.iniciarSesion(usuario, contrasena) ? 1 : -1;
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error al iniciar sesión.", e);
            return -1;
        }
    }

    @WebMethod(operationName = "crearSesion")
    @WebResult(name = "token")
    public String crearSesion(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasena") String contrasena) {
        try {
            return service.crearSesion(usuario, contrasena);
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error al crear sesión segura.", e);
            return null;
        }
    }

    @WebMethod(operationName = "traerCuentas")
    @WebResult(name = "cuenta")
    public List<CuentaDto> traerCuentas(@WebParam(name = "token") String token) {
        try { return service.traerCuentas(token); }
        catch (SecurityException e) { throw new WebServiceException(e.getMessage()); }
        catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error al consultar cuentas.", e);
            throw new WebServiceException("No se pudieron consultar las cuentas.", e);
        }
    }

    @WebMethod(operationName = "traerMovimientos")
    @WebResult(name = "movimiento")
    public List<Movimiento> traerMovimientos(@WebParam(name = "token") String token, @WebParam(name = "cuenta") String cuenta) {
        try {
            return service.traerMovimientos(token, cuenta);
        } catch (SecurityException e) {
            throw new WebServiceException(e.getMessage());
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error al consultar movimientos.", e);
            throw new WebServiceException("No se pudieron consultar los movimientos.", e);
        }
    }

    @WebMethod(operationName = "regDeposito")
    @WebResult(name = "resultado")
    public int regDeposito(
            @WebParam(name = "token") String token,
            @WebParam(name = "cuenta") String cuenta,
            @WebParam(name = "importe") BigDecimal importe) {
        try {
            return service.registrarDeposito(token, cuenta, importe);
        } catch (SecurityException e) {
            throw new WebServiceException(e.getMessage());
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar el depósito.", e);
            return -1;
        }
    }

    @WebMethod(operationName = "transferir")
    @WebResult(name = "resultado")
    public int transferir(
            @WebParam(name = "token") String token,
            @WebParam(name = "cuentaOrigen") String cuentaOrigen,
            @WebParam(name = "cuentaDestino") String cuentaDestino,
            @WebParam(name = "importe") BigDecimal importe,
            @WebParam(name = "claveOperacion") String claveOperacion) {
        try {
            return service.transferir(token, cuentaOrigen, cuentaDestino, importe, claveOperacion) ? 1 : -1;
        } catch (SecurityException e) {
            throw new WebServiceException(e.getMessage());
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar la transferencia.", e);
            return -1;
        }
    }

    @WebMethod(operationName = "cerrarSesion")
    @WebResult(name = "resultado")
    public int cerrarSesion(@WebParam(name = "token") String token) {
        service.cerrarSesion(token);
        return 1;
    }
}
