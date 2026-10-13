package ec.edu.monster.cliente.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService(name = "WSConversionUnidades_Java_SOAP_GR10", targetNamespace = "http://ws.controladores.monster.edu.ec/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ContratoWSConversor {

    @WebMethod(operationName = "login")
    @WebResult(name = "return")
    String login(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "contrasenia") String contrasenia);

    @WebMethod(operationName = "cambiarContrasenia")
    @WebResult(name = "return")
    String cambiarContrasenia(
            @WebParam(name = "contraseniaActual") String contraseniaActual,
            @WebParam(name = "contraseniaNueva") String contraseniaNueva);

    @WebMethod(operationName = "convertirLongitud")
    @WebResult(name = "return")
    double convertirLongitud(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal);

    @WebMethod(operationName = "convertirMasa")
    @WebResult(name = "return")
    double convertirMasa(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal);

    @WebMethod(operationName = "convertirTemperatura")
    @WebResult(name = "return")
    double convertirTemperatura(
            @WebParam(name = "valor") double valor,
            @WebParam(name = "unidadInicial") String unidadInicial,
            @WebParam(name = "unidadFinal") String unidadFinal);
}
