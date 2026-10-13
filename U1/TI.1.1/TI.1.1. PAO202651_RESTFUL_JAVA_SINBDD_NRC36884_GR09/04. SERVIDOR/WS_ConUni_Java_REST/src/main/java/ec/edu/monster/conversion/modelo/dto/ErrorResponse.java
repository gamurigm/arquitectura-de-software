package ec.edu.monster.conversion.modelo.dto;

import java.time.OffsetDateTime;

/**
 * Representacion JSON estandarizada de un error.
 */
public record ErrorResponse(
        boolean exito,
        int codigo,
        String error,
        String mensaje,
        String ruta,
        OffsetDateTime timestamp) {
}
