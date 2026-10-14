package ec.edu.monster.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import ec.edu.monster.modelo.SolicitudConversion;
import ec.edu.monster.servicios.ClienteApi;
import ec.edu.monster.servicios.ExcepcionApi;
import java.io.IOException;

public class ControladorCliente {
    private final ClienteApi api;

    public ControladorCliente(String url) {
        api = new ClienteApi(url);
    }

    public String iniciarSesion(String usuario, String clave) throws IOException, InterruptedException, ExcepcionApi {
        return api.iniciarSesion(usuario, clave);
    }

    public JsonNode obtenerCatalogo() throws IOException, InterruptedException, ExcepcionApi {
        return api.obtenerCatalogo();
    }

    public JsonNode convertir(String magnitud, double valor, String origen, String destino, String token)
            throws IOException, InterruptedException, ExcepcionApi {
        SolicitudConversion solicitud = new SolicitudConversion(magnitud, valor, origen, destino);
        return api.convertir(solicitud.magnitud(), solicitud.valor(),
                solicitud.origen(), solicitud.destino(), token);
    }
}
