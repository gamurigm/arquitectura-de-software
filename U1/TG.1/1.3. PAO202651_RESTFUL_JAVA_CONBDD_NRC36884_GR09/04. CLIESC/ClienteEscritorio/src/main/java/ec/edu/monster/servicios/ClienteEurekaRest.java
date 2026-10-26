package ec.edu.monster.servicios;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Cuenta;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public final class ClienteEurekaRest {
    private static final String URL_PREDETERMINADA = "http://127.0.0.1:8080/WS_EurekaBank_REST_JAVA/api/eureka";
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    private final ObjectMapper json = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String baseUrl;

    public ClienteEurekaRest() {
        String url = System.getenv().getOrDefault("EUREKABANK_REST_URL", URL_PREDETERMINADA).strip();
        URI uri = URI.create(url);
        if (uri.getHost() == null || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
            throw new IllegalArgumentException("EUREKABANK_REST_URL debe ser una URL HTTP o HTTPS.");
        }
        baseUrl = url.replaceAll("/+$", "");
    }

    public String iniciarSesion(String usuario, String contrasena) throws IOException, InterruptedException {
        return enviar("POST", "/login", Map.of("usuario", usuario, "contrasena", contrasena)).path("token").asText(null);
    }

    public List<Movimiento> traerMovimientos(String token, String cuenta) throws IOException, InterruptedException {
        String url = baseUrl + "/movimientos?cuenta=" + java.net.URLEncoder.encode(cuenta, StandardCharsets.UTF_8);
        HttpResponse<String> respuesta = enviarHttp(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8))
                .header("Authorization", "Bearer " + token).GET().build());
        revisar(respuesta);
        return json.readValue(respuesta.body(), new TypeReference<>() { });
    }

    public List<Cuenta> traerCuentas(String token) throws IOException, InterruptedException {
        String url = baseUrl + "/cuentas";
        HttpResponse<String> respuesta = enviarHttp(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8))
                .header("Authorization", "Bearer " + token).GET().build());
        revisar(respuesta);
        return json.readValue(respuesta.body(), new TypeReference<>() { });
    }

    public boolean registrarDeposito(String token, String cuenta, BigDecimal importe) throws IOException, InterruptedException {
        return enviar(token, "POST", "/depositos", Map.of("cuenta", cuenta, "importe", importe)).path("registrado").asBoolean(false);
    }

    public boolean transferir(String token, String origen, String destino, BigDecimal importe, String claveOperacion) throws IOException, InterruptedException {
        return enviar(token, "POST", "/transferencias", Map.of("cuentaOrigen", origen, "cuentaDestino", destino,
                "importe", importe, "claveOperacion", claveOperacion)).path("registrada").asBoolean(false);
    }

    public void cerrarSesion(String token) throws IOException, InterruptedException { enviar(token, "POST", "/logout", Map.of()); }

    private JsonNode enviar(String metodo, String ruta, Object cuerpo) throws IOException, InterruptedException {
        return enviar(null, metodo, ruta, cuerpo);
    }

    private JsonNode enviar(String token, String metodo, String ruta, Object cuerpo) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl + ruta)).timeout(Duration.ofSeconds(8))
                .header("Content-Type", "application/json");
        if (token != null && !token.isBlank()) builder.header("Authorization", "Bearer " + token);
        HttpRequest solicitud = builder
                .method(metodo, HttpRequest.BodyPublishers.ofString(json.writeValueAsString(cuerpo))).build();
        HttpResponse<String> respuesta = enviarHttp(solicitud);
        revisar(respuesta);
        return json.readTree(respuesta.body());
    }

    private HttpResponse<String> enviarHttp(HttpRequest solicitud) throws IOException, InterruptedException {
        try {
            return http.send(solicitud, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            if (e.getMessage() != null && !e.getMessage().isBlank()) throw e;
            throw new IOException("Conexión rechazada o servicio REST no disponible. Inicie el servidor y verifique EUREKABANK_REST_URL.", e);
        }
    }

    private static void revisar(HttpResponse<String> respuesta) throws IOException {
        if (respuesta.statusCode() < 200 || respuesta.statusCode() >= 300) throw new IOException("El servidor respondió HTTP " + respuesta.statusCode() + ".");
    }
}
