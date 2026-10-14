package ec.edu.monster.modelo.dto;

import java.time.OffsetDateTime;

/**
 * Representacion JSON estandarizada de un error.
 */
public record RespuestaError(
        boolean exito,
        int codigo,
        String error,
        String mensaje,
        String ruta,
        OffsetDateTime timestamp) {
}
