package ec.edu.monster.conversion;

import ec.edu.monster.conversion.modelo.dto.ConversionResponse;
import ec.edu.monster.conversion.modelo.dto.TokenResponse;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de integracion extremo a extremo: login, autorizacion por token y conversiones.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConversionApiTest {

    @Autowired
    private TestRestTemplate rest;

    private String iniciarSesion() {
        Map<String, String> credenciales = Map.of("usuario", "monster", "clave", "monster9");
        ResponseEntity<TokenResponse> respuesta = rest.postForEntity("/api/auth/login", credenciales, TokenResponse.class);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        return respuesta.getBody().token();
    }

    @Test
    void loginCorrectoEntregaToken() {
        assertNotNull(iniciarSesion());
    }

    @Test
    void loginIncorrectoDevuelve401() {
        Map<String, String> credenciales = Map.of("usuario", "monster", "clave", "incorrecta");
        ResponseEntity<String> respuesta = rest.postForEntity("/api/auth/login", credenciales, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
    }

    @Test
    void conversionSinTokenDevuelve401() {
        Map<String, Object> cuerpo = Map.of("valor", 10, "unidadOrigen", "metros", "unidadDestino", "pies");
        ResponseEntity<String> respuesta = rest.postForEntity("/api/conversiones/longitud", cuerpo, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
    }

    @Test
    void conversionConTokenDevuelveResultado() {
        String token = iniciarSesion();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        Map<String, Object> cuerpo = Map.of("valor", 10, "unidadOrigen", "metros", "unidadDestino", "pies");

        ResponseEntity<ConversionResponse> respuesta = rest.exchange(
                "/api/conversiones/longitud", HttpMethod.POST, new HttpEntity<>(cuerpo, headers), ConversionResponse.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(32.808399, respuesta.getBody().resultado(), 0.001);
    }

    @Test
    void unidadInvalidaDevuelve400() {
        String token = iniciarSesion();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        Map<String, Object> cuerpo = Map.of("valor", 10, "unidadOrigen", "metros", "unidadDestino", "leguas");

        ResponseEntity<String> respuesta = rest.exchange(
                "/api/conversiones/longitud", HttpMethod.POST, new HttpEntity<>(cuerpo, headers), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().contains("Unidad invalida"));
    }

    @Test
    void temperaturaConToken() {
        String token = iniciarSesion();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        Map<String, Object> cuerpo = Map.of("valor", 100, "unidadOrigen", "celsius", "unidadDestino", "fahrenheit");

        ResponseEntity<ConversionResponse> respuesta = rest.exchange(
                "/api/conversiones/temperatura", HttpMethod.POST, new HttpEntity<>(cuerpo, headers), ConversionResponse.class);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(212.0, respuesta.getBody().resultado(), 0.0001);
    }
}
