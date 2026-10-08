package ec.edu.monster.controladores.ws;

import ec.edu.monster.controladores.manejadoresws.ManejadorAutenticacion;
import ec.edu.monster.modelos.utilidades.ConversorLongitud;
import ec.edu.monster.modelos.utilidades.ConversorMasa;
import ec.edu.monster.modelos.utilidades.ConversorTemperatura;
import ec.edu.monster.modelos.utilidades.enums.UnidadLongitud;
import ec.edu.monster.modelos.utilidades.enums.UnidadMasa;
import ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;
import ec.edu.monster.modelos.utilidades.mapeadores.UnidadMapper;
import ec.edu.monster.seguridad.AdministradorCredenciales;
import ec.edu.monster.seguridad.AdministradorTokens;
import ec.edu.monster.servicios.ServicioConversor;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(
        serviceName = "WSConversionUnidades_Java_SOAP_GR10",
        targetNamespace = "http://ws.controladores.monster.edu.ec/"
)
@HandlerChain(file = "manejador.xml")
public class WSConversionUnidades_Java_SOAP_GR10 {

    private final ServicioConversor<UnidadLongitud> servicioLongitud =
            new ServicioConversor<>(new ConversorLongitud());
    private final ServicioConversor<UnidadMasa> servicioMasa =
            new ServicioConversor<>(new ConversorMasa());
    private final ServicioConversor<UnidadTemperatura> servicioTemperatura =
            new ServicioConversor<>(new ConversorTemperatura());

    @WebMethod(operationName = "login")
    public String login(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasenia") String contrasenia) {
        if (!AdministradorCredenciales.validarCredenciales(usuario, contrasenia)) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }
        return AdministradorTokens.generarToken();
    }

    @WebMethod(operationName = "cambiarContrasenia")
    public String cambiarContrasenia(
            @WebParam(name = "contraseniaActual") String contraseniaActual,
            @WebParam(name = "contraseniaNueva") String contraseniaNueva) {
        AdministradorCredenciales.cambiarContrasenia(contraseniaActual, contraseniaNueva);
        return "Contrasenia actualizada correctamente";
    }

    @WebMethod(operationName = "convertirLongitud")
    public double convertirLongitud(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal) {
        return servicioLongitud.convertir(
                valor,
                UnidadMapper.toLongitud(unidadInicial),
                UnidadMapper.toLongitud(unidadFinal));
    }

    @WebMethod(operationName = "convertirMasa")
    public double convertirMasa(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal) {
        return servicioMasa.convertir(
                valor,
                UnidadMapper.toMasa(unidadInicial),
                UnidadMapper.toMasa(unidadFinal));
    }

    @WebMethod(operationName = "convertirTemperatura")
    public double convertirTemperatura(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal) {
        return servicioTemperatura.convertir(
                valor,
                UnidadMapper.toTemperatura(unidadInicial),
                UnidadMapper.toTemperatura(unidadFinal));
    }
}