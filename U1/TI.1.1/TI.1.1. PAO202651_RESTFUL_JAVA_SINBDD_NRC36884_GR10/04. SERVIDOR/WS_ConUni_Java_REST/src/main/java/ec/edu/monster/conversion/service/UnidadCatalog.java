package ec.edu.monster.conversion.service;

import ec.edu.monster.conversion.model.Magnitud;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Catalogo de unidades y factores de conversion a la unidad base.
 * Longitud -&gt; base metros. Masa -&gt; base kilogramos.
 * Temperatura se resuelve con formulas no lineales en {@link ConversionService}.
 */
@Component
public class UnidadCatalog {

    private final Map<String, Double> longitud = new LinkedHashMap<>();
    private final Map<String, Double> masa = new LinkedHashMap<>();

    public UnidadCatalog() {
        // Longitud - base metros
        longitud.put("centimetros", 0.01);
        longitud.put("metros", 1.0);
        longitud.put("pies", 0.3048);
        longitud.put("yardas", 0.9144);
        longitud.put("millas", 1609.344);

        // Masa - base kilogramos
        masa.put("gramos", 0.001);
        masa.put("kilogramos", 1.0);
        masa.put("onzas", 0.028349523125);
        masa.put("libras", 0.45359237);
        masa.put("quintales", 100.0);
    }

    private final List<String> temperatura = List.of(
            "fahrenheit", "kelvin", "celsius", "newton", "reaumur"
    );

    public Map<String, Double> factores(Magnitud magnitud) {
        return switch (magnitud) {
            case LONGITUD -> longitud;
            case MASA -> masa;
            case TEMPERATURA -> Map.of();
        };
    }

    public List<String> unidades(Magnitud magnitud) {
        return switch (magnitud) {
            case LONGITUD -> List.copyOf(longitud.keySet());
            case MASA -> List.copyOf(masa.keySet());
            case TEMPERATURA -> temperatura;
        };
    }

    public boolean esUnidadValida(Magnitud magnitud, String unidad) {
        if (unidad == null) {
            return false;
        }
        String normalizada = unidad.trim().toLowerCase();
        if (magnitud == Magnitud.TEMPERATURA) {
            return temperatura.contains(normalizada);
        }
        return factores(magnitud).containsKey(normalizada);
    }

    public Map<String, List<String>> catalogo() {
        Map<String, List<String>> mapa = new LinkedHashMap<>();
        mapa.put("longitud", unidades(Magnitud.LONGITUD));
        mapa.put("masa", unidades(Magnitud.MASA));
        mapa.put("temperatura", unidades(Magnitud.TEMPERATURA));
        return mapa;
    }
}
