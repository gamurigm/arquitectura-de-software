package ec.edu.monster.cliweb;

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
public class ApiClient {

    private final String baseUrl;
    private final HttpClient http;
    private final ObjectMapper mapper = new ObjectMapper();

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    public String login(String usuario, String clave) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("usuario", usuario);
        body.put("clave", clave);
        return post("/api/auth/login", body, null).path("token").asText();
    }

    public JsonNode catalogo() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/api/catalogo"))
                .timeout(Duration.ofSeconds(10)).GET().build();
        return enviar(req).body();
    }

    public JsonNode convertir(String magnitud, double valor, String origen, String destino, String token) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("valor", valor);
        body.put("unidadOrigen", origen);
        body.put("unidadDestino", destino);
        return post("/api/conversiones/" + magnitud, body, token);
    }

    private JsonNode post(String path, Map<String, Object> body, String token) throws Exception {
        String json = mapper.writeValueAsString(body);
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json));
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return enviar(builder.build()).body();
    }

    private Respuesta enviar(HttpRequest req) throws Exception {
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode json = resp.body() == null || resp.body().isBlank()
                ? mapper.createObjectNode()
                : mapper.readTree(resp.body());
        if (resp.statusCode() >= 400) {
            throw new ApiException(resp.statusCode(), json.path("mensaje").asText("Error HTTP " + resp.statusCode()));
        }
        return new Respuesta(resp.statusCode(), json);
    }

    public record Respuesta(int status, JsonNode body) {
    }
}
