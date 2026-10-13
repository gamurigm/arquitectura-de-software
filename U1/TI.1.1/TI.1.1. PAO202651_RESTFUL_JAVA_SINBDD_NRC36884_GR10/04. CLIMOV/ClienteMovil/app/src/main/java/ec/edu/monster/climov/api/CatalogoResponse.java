package ec.edu.monster.climov.api;

import java.util.List;
import java.util.Map;

/** Respuesta del endpoint /api/catalogo. */
public class CatalogoResponse {

    private Map<String, List<String>> magnitudes;

    public Map<String, List<String>> getMagnitudes() {
        return magnitudes;
    }
}
