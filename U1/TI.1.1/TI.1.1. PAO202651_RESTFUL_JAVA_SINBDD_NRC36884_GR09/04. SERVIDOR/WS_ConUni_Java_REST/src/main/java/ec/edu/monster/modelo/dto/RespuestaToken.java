package ec.edu.monster.modelo.dto;

/**
 * Respuesta de autenticación con el token emitido.
 */
public record RespuestaToken(
        boolean exito,
        String token,
        String tipo,
        String usuario,
        long expiraEnSegundos,
        String mensaje) {
}
