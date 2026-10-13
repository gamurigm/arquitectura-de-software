package ec.edu.monster.cliente.servicios;

import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.handler.Handler;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;

public final class ClienteSOAP {

    public static final String NAMESPACE = "http://ws.controladores.monster.edu.ec/";
    private static final QName SERVICIO = new QName(NAMESPACE, "WSConversionUnidades_Java_SOAP_GR10");
    private static final QName PUERTO = new QName(NAMESPACE, "WSConversionUnidades_Java_SOAP_GR10Port");
    private static final String DIRECCION_SERVICIO_PREDETERMINADA =
            "http://127.0.0.1:19080/04.SERVIDOR/WSConversionUnidades_Java_SOAP_GR10";

    private final ContratoWSConversor puerto;

    public ClienteSOAP(String token) {
        String direccionServicio = System.getProperty("ws.base.url");
        if (direccionServicio == null || direccionServicio.isBlank()) {
            direccionServicio = System.getenv("WS_BASE_URL");
        }
        if (direccionServicio == null || direccionServicio.isBlank()) {
            direccionServicio = DIRECCION_SERVICIO_PREDETERMINADA;
        }
        direccionServicio = direccionServicio.trim().replaceAll("/+$", "");

        try {
            Service servicio = Service.create(new URL(direccionServicio + "?WSDL"), SERVICIO);
            puerto = servicio.getPort(PUERTO, ContratoWSConversor.class);
            BindingProvider proveedor = (BindingProvider) puerto;
            proveedor.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, direccionServicio);
            List<Handler> manejadores = new ArrayList<>(proveedor.getBinding().getHandlerChain());
            manejadores.add(new ManejadorTokenSOAP(token));
            proveedor.getBinding().setHandlerChain(manejadores);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "No se pudo conectar al servicio SOAP GR10 en " + direccionServicio, ex);
        }
    }

    public String login(String usuario, String contrasenia) {
        return puerto.login(usuario, contrasenia);
    }

    public double convertir(String tipo, double valor, String origen, String destino) {
        return switch (tipo) {
            case "longitud" -> puerto.convertirLongitud(valor, origen, destino);
            case "masa" -> puerto.convertirMasa(valor, origen, destino);
            case "temperatura" -> puerto.convertirTemperatura(valor, origen, destino);
            default -> throw new IllegalArgumentException("Tipo de conversión no válido");
        };
    }
}
