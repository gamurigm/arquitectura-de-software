package ec.edu.monster;

import ec.edu.monster.modelo.dto.RespuestaConversion;
import ec.edu.monster.modelo.dto.RespuestaToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de integración de extremo a extremo: inicio de sesión, autorización por token y conversiones.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.servlet.context-path=/WS_ConUni_Java_REST")
class PruebaApiConversion {

    @Autowired
    private TestRestTemplate clienteRest;

    @NonNull
    private String iniciarSesion() {
        Map<String, String> credenciales = Map.of("usuario", "Monster", "clave", "Monster9");
        ResponseEntity<RespuestaToken> respuesta = clienteRest.postForEntity("/api/auth/login", credenciales, RespuestaToken.class);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        RespuestaToken cuerpo = Objects.requireNonNull(respuesta.getBody());
        return Objects.requireNonNull(cuerpo.token());
    }

    @Test
    void inicioSesionCorrectoEntregaToken() {
        assertNotNull(iniciarSesion());
    }

    @Test
    void inicioSesionIncorrectoDevuelve401() {
        Map<String, String> credenciales = Map.of("usuario", "Monster", "clave", "incorrecta");
        ResponseEntity<String> respuesta = clienteRest.postForEntity("/api/auth/login", credenciales, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
    }

    @Test
    void conversionSinTokenDevuelve401() {
        Map<String, Object> cuerpo = Map.of("valor", 10, "unidadOrigen", "metros", "unidadDestino", "pies");
        ResponseEntity<String> respuesta = clienteRest.postForEntity("/api/conversiones/longitud", cuerpo, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
    }

    @Test
    void conversionConTokenDevuelveResultado() {
        String token = iniciarSesion();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        Map<String, Object> cuerpo = Map.of("valor", 10, "unidadOrigen", "metros", "unidadDestino", "pies");

        ResponseEntity<RespuestaConversion> respuesta = clienteRest.exchange(
                "/api/conversiones/longitud", HttpMethod.POST, new HttpEntity<>(cuerpo, headers), RespuestaConversion.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(32.808399, Objects.requireNonNull(respuesta.getBody()).resultado(), 0.001);
    }

    @Test
    void unidadInvalidaDevuelve400() {
        String token = iniciarSesion();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        Map<String, Object> cuerpo = Map.of("valor", 10, "unidadOrigen", "metros", "unidadDestino", "leguas");

        ResponseEntity<String> respuesta = clienteRest.exchange(
                "/api/conversiones/longitud", HttpMethod.POST, new HttpEntity<>(cuerpo, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(Objects.requireNonNull(respuesta.getBody()).contains("Unidad invalida"));
    }

    @Test
    void temperaturaConToken() {
        String token = iniciarSesion();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        Map<String, Object> cuerpo = Map.of("valor", 100, "unidadOrigen", "celsius", "unidadDestino", "fahrenheit");

        ResponseEntity<RespuestaConversion> respuesta = clienteRest.exchange(
                "/api/conversiones/temperatura", HttpMethod.POST, new HttpEntity<>(cuerpo, headers), RespuestaConversion.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(212.0, Objects.requireNonNull(respuesta.getBody()).resultado(), 0.0001);
    }
}
