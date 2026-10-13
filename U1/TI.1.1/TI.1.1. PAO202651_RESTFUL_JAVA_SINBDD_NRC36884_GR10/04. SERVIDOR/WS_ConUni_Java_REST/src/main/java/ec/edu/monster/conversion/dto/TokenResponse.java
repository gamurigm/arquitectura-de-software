package ec.edu.monster.conversion.dto;

/**
 * Respuesta de autenticacion con el token emitido.
 */
public record TokenResponse(
        boolean exito,
        String token,
        String tipo,
        String usuario,
        long expiraEnSegundos,
        String mensaje) {
}
