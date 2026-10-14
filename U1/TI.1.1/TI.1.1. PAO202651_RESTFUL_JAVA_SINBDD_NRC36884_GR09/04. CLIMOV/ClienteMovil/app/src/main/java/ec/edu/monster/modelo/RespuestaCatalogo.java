package ec.edu.monster.modelo;

import java.util.List;
import java.util.Map;

/** Respuesta del endpoint /api/catalogo. */
public class RespuestaCatalogo {

    private Map<String, List<String>> magnitudes;

    public Map<String, List<String>> obtenerMagnitudes() {
        return magnitudes;
    }
}
