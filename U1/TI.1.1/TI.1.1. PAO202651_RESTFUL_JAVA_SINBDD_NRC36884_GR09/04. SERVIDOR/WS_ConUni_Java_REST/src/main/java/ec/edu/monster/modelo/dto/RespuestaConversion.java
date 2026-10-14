package ec.edu.monster.modelo.dto;

/**
 * Representacion JSON estandarizada de una conversion exitosa.
 */
public record RespuestaConversion(
        boolean exito,
        String magnitud,
        double valor,
        String unidadOrigen,
        String unidadDestino,
        double resultado,
        String mensaje) {
}
