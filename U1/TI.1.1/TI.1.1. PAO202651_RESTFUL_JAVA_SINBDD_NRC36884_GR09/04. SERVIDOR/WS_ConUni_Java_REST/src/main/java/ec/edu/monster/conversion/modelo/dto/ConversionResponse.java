package ec.edu.monster.conversion.modelo.dto;

/**
 * Representacion JSON estandarizada de una conversion exitosa.
 */
public record ConversionResponse(
        boolean exito,
        String magnitud,
        double valor,
        String unidadOrigen,
        String unidadDestino,
        double resultado,
        String mensaje) {
}
