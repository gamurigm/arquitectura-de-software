package ec.edu.monster.servicios;

import ec.edu.monster.modelo.Magnitud;
import ec.edu.monster.modelo.UnidadLongitud;
import ec.edu.monster.modelo.UnidadMasa;
import ec.edu.monster.modelo.UnidadTemperatura;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Catalogo de unidades disponibles para cada magnitud.
 */
@Component
public class CatalogoUnidades {

    private static final List<String> LONGITUD = Arrays.stream(UnidadLongitud.values())
            .map(unidad -> unidad.name().toLowerCase(Locale.ROOT)).toList();
    private static final List<String> MASA = Arrays.stream(UnidadMasa.values())
            .map(unidad -> unidad.name().toLowerCase(Locale.ROOT)).toList();
    private static final List<String> TEMPERATURA = Arrays.stream(UnidadTemperatura.values())
            .map(unidad -> unidad.name().toLowerCase(Locale.ROOT)).toList();

    public List<String> unidades(Magnitud magnitud) {
        return switch (magnitud) {
            case LONGITUD -> LONGITUD;
            case MASA -> MASA;
            case TEMPERATURA -> TEMPERATURA;
        };
    }

    public boolean esUnidadValida(Magnitud magnitud, String unidad) {
        if (unidad == null) {
            return false;
        }
        String normalizada = unidad.trim().toLowerCase(Locale.ROOT);
        return unidades(magnitud).contains(normalizada);
    }

    public Map<String, List<String>> obtenerCatalogo() {
        Map<String, List<String>> mapa = new LinkedHashMap<>();
        mapa.put("longitud", unidades(Magnitud.LONGITUD));
        mapa.put("masa", unidades(Magnitud.MASA));
        mapa.put("temperatura", unidades(Magnitud.TEMPERATURA));
        return mapa;
    }
}
