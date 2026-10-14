package ec.edu.monster.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cliente HTTP que consume la API REST del sistema Monster desde los servlets.
 */
public class ClienteApi {

    private final String urlBase;
    private final HttpClient clienteHttp;
    private final ObjectMapper serializadorJson = new ObjectMapper();

    public ClienteApi(String urlBase) {
        this.urlBase = validarUrlBase(urlBase);
        this.clienteHttp = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    public String iniciarSesion(String usuario, String clave) throws Exception {
        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank()) {
            throw new IllegalArgumentException("Ingrese usuario y contraseña.");
        }
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("usuario", usuario);
        cuerpo.put("clave", clave);
        return enviarPost("/api/auth/login", cuerpo, null).path("token").asText();
    }

    public JsonNode obtenerCatalogo() throws Exception {
        HttpRequest solicitudHttp = HttpRequest.newBuilder(URI.create(urlBase + "/api/catalogo"))
                .timeout(Duration.ofSeconds(10)).GET().build();
        return enviar(solicitudHttp).cuerpo();
    }

    public JsonNode convertir(String magnitud, double valor, String origen, String destino, String token) throws Exception {
        if (!"longitud".equals(magnitud) && !"masa".equals(magnitud) && !"temperatura".equals(magnitud)) {
            throw new IllegalArgumentException("Seleccione una magnitud válida.");
        }
        if (!Double.isFinite(valor) || origen == null || origen.isBlank() || destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("Ingrese un número finito y seleccione ambas unidades.");
        }
        if ("masa".equals(magnitud) && valor < 0) {
            throw new IllegalArgumentException("La masa no puede ser negativa.");
        }
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("valor", valor);
        cuerpo.put("unidadOrigen", origen);
        cuerpo.put("unidadDestino", destino);
        return enviarPost("/api/conversiones/" + magnitud, cuerpo, token);
    }

    private JsonNode enviarPost(String ruta, Map<String, Object> cuerpo, String token) throws Exception {
        String json = serializadorJson.writeValueAsString(cuerpo);
        HttpRequest.Builder constructorSolicitud = HttpRequest.newBuilder(URI.create(urlBase + ruta))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (token != null) {
            constructorSolicitud.header("Authorization", "Bearer " + token);
        }
        return enviar(constructorSolicitud.build()).cuerpo();
    }

    private Respuesta enviar(HttpRequest solicitudHttp) throws Exception {
        HttpResponse<String> respuestaHttp = clienteHttp.send(solicitudHttp, HttpResponse.BodyHandlers.ofString());
        JsonNode json = respuestaHttp.body() == null || respuestaHttp.body().isBlank()
                ? serializadorJson.createObjectNode()
                : serializadorJson.readTree(respuestaHttp.body());
        if (respuestaHttp.statusCode() >= 400) {
            throw new ExcepcionApi(respuestaHttp.statusCode(), json.path("mensaje").asText("Error HTTP " + respuestaHttp.statusCode()));
        }
        return new Respuesta(respuestaHttp.statusCode(), json);
    }

    private static String validarUrlBase(String valor) {
        try {
            URI uri = URI.create(valor == null ? "" : valor.trim());
            if (!uri.isAbsolute() || uri.getHost() == null || uri.getUserInfo() != null
                    || uri.getQuery() != null || uri.getFragment() != null
                    || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
                throw new IllegalArgumentException();
            }
            String normalizada = uri.toString();
            return normalizada.endsWith("/") ? normalizada.substring(0, normalizada.length() - 1) : normalizada;
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Ingrese una URL HTTP o HTTPS válida.", ex);
        }
    }

    public record Respuesta(int estadoHttp, JsonNode cuerpo) {
    }
}
