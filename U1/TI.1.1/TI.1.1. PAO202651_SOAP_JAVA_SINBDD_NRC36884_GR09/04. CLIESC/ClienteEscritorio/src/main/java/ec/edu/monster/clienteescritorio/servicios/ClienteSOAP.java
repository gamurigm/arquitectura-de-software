package ec.edu.monster.clienteescritorio.servicios;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.handler.Handler;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public final class ClienteSOAP {

    public static final String ESPACIO_NOMBRES = "http://ws.controladores.monster.edu.ec/";
    private static final QName NOMBRE_SERVICIO =
            new QName(ESPACIO_NOMBRES, "WSConversionUnidades_Java_SOAP_GR09");
    private static final QName NOMBRE_PUERTO =
            new QName(ESPACIO_NOMBRES, "WSConversionUnidades_Java_SOAP_GR09Port");
    private static final String URL_PREDETERMINADA =
            "http://127.0.0.1:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR09";

    private final ContratoWSConversor puerto;
    private String token;

    public ClienteSOAP() {
        String direccionServicio = System.getProperty("ws.base.url");
        if (direccionServicio == null || direccionServicio.isBlank()) {
            direccionServicio = System.getenv("WS_BASE_URL");
        }
        if (direccionServicio == null || direccionServicio.isBlank()) {
            direccionServicio = URL_PREDETERMINADA;
        }
        direccionServicio = direccionServicio.trim().replaceAll("/+$", "");

        try {
            Service servicio = Service.create(new URL(direccionServicio + "?WSDL"), NOMBRE_SERVICIO);
            puerto = servicio.getPort(NOMBRE_PUERTO, ContratoWSConversor.class);
            BindingProvider proveedor = (BindingProvider) puerto;
            proveedor.getRequestContext().put(
                    BindingProvider.ENDPOINT_ADDRESS_PROPERTY, direccionServicio);
            List<Handler> manejadores = new ArrayList<>(proveedor.getBinding().getHandlerChain());
            manejadores.add(new ManejadorTokenSOAP(() -> token));
            proveedor.getBinding().setHandlerChain(manejadores);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "No se pudo conectar al servicio SOAP GR09 en " + direccionServicio, ex);
        }
    }

    public String iniciarSesion(String usuario, String contrasenia) {
        token = puerto.login(usuario, contrasenia);
        return token;
    }

    public double convertir(String tipo, double valor, String unidadInicial, String unidadFinal) {
        return switch (tipo) {
            case "longitud" -> puerto.convertirLongitud(valor, unidadInicial, unidadFinal);
            case "masa" -> puerto.convertirMasa(valor, unidadInicial, unidadFinal);
            case "temperatura" -> puerto.convertirTemperatura(valor, unidadInicial, unidadFinal);
            default -> throw new IllegalArgumentException("Tipo de conversión no válido.");
        };
    }

    public void cerrarSesion() {
        token = null;
    }

    public String obtenerToken() {
        return token;
    }
}
