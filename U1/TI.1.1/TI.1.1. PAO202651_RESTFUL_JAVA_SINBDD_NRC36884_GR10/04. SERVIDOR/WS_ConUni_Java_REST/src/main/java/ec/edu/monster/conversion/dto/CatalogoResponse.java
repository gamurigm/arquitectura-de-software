package ec.edu.monster.conversion.dto;

import java.util.List;
import java.util.Map;

/**
 * Catalogo de unidades disponibles agrupadas por magnitud.
 */
public record CatalogoResponse(
        boolean exito,
        Map<String, List<String>> magnitudes,
        String mensaje) {
}
