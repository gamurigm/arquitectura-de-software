package ec.edu.monster.cliesc.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import ec.edu.monster.cliesc.modelo.SolicitudConversion;
import ec.edu.monster.cliesc.servicios.ApiClient;
import ec.edu.monster.cliesc.servicios.ApiException;
import java.io.IOException;

public class ControladorCliente {
    private final ApiClient api;

    public ControladorCliente(String url) {
        api = new ApiClient(url);
    }

    public String login(String usuario, String clave) throws IOException, InterruptedException, ApiException {
        return api.login(usuario, clave);
    }

    public JsonNode catalogo() throws IOException, InterruptedException, ApiException {
        return api.catalogo();
    }

    public JsonNode convertir(String magnitud, double valor, String origen, String destino, String token)
            throws IOException, InterruptedException, ApiException {
        SolicitudConversion solicitud = new SolicitudConversion(magnitud, valor, origen, destino);
        return api.convertir(solicitud.magnitud(), solicitud.valor(),
                solicitud.origen(), solicitud.destino(), token);
    }
}
