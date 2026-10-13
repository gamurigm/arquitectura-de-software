package ec.edu.monster.controladores.ws;

import ec.edu.monster.controladores.manejadoresws.ManejadorAutenticacion;
import ec.edu.monster.modelos.utilidades.ConversorLongitud;
import ec.edu.monster.modelos.utilidades.ConversorMasa;
import ec.edu.monster.modelos.utilidades.ConversorTemperatura;
import ec.edu.monster.modelos.utilidades.enumeraciones.UnidadLongitud;
import ec.edu.monster.modelos.utilidades.enumeraciones.UnidadMasa;
import ec.edu.monster.modelos.utilidades.enumeraciones.UnidadTemperatura;
import ec.edu.monster.modelos.utilidades.mapeadores.MapeadorUnidades;
import ec.edu.monster.seguridad.AdministradorCredenciales;
import ec.edu.monster.seguridad.AdministradorTokens;
import ec.edu.monster.servicios.ServicioConversor;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(
        serviceName = "WSConversionUnidades_Java_SOAP_GR09",
        targetNamespace = "http://ws.controladores.monster.edu.ec/"
)
@HandlerChain(file = "manejador.xml")
public class WSConversionUnidades_Java_SOAP_GR09 {

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
        validarValor(valor);
        return servicioLongitud.convertir(
                valor,
                MapeadorUnidades.aLongitud(unidadInicial),
                MapeadorUnidades.aLongitud(unidadFinal));
    }

    @WebMethod(operationName = "convertirMasa")
    public double convertirMasa(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal) {
        validarValor(valor);
        if (valor < 0) {
            throw new IllegalArgumentException("La masa no puede ser negativa");
        }
        return servicioMasa.convertir(
                valor,
                MapeadorUnidades.aMasa(unidadInicial),
                MapeadorUnidades.aMasa(unidadFinal));
    }

    @WebMethod(operationName = "convertirTemperatura")
    public double convertirTemperatura(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal) {
        validarValor(valor);
        return servicioTemperatura.convertir(
                valor,
                MapeadorUnidades.aTemperatura(unidadInicial),
                MapeadorUnidades.aTemperatura(unidadFinal));
    }

    private static void validarValor(double valor) {
        if (!Double.isFinite(valor)) {
            throw new IllegalArgumentException("El valor debe ser un número finito");
        }
    }
}
