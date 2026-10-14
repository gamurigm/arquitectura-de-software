package ec.edu.monster.modelo.dto;

import java.util.List;
import java.util.Map;

/**
 * Catalogo de unidades disponibles agrupadas por magnitud.
 */
public record RespuestaCatalogo(
        boolean exito,
        Map<String, List<String>> magnitudes,
        String mensaje) {
}
