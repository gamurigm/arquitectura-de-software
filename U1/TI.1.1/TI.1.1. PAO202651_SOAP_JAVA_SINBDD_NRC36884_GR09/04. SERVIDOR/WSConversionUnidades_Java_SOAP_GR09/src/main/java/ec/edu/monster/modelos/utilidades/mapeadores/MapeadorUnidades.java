package ec.edu.monster.modelos.utilidades.mapeadores;

import ec.edu.monster.modelos.utilidades.enumeraciones.UnidadLongitud;
import ec.edu.monster.modelos.utilidades.enumeraciones.UnidadMasa;
import ec.edu.monster.modelos.utilidades.enumeraciones.UnidadTemperatura;
import java.util.Locale;

public final class MapeadorUnidades {

    private MapeadorUnidades() {
    }

    public static UnidadLongitud aLongitud(String unidad) {
        return parsear(unidad, UnidadLongitud.class);
    }

    public static UnidadMasa aMasa(String unidad) {
        return parsear(unidad, UnidadMasa.class);
    }

    public static UnidadTemperatura aTemperatura(String unidad) {
        return parsear(unidad, UnidadTemperatura.class);
    }

    private static <T extends Enum<T>> T parsear(String unidad, Class<T> tipo) {
        if (unidad == null || unidad.isBlank()) {
            throw new IllegalArgumentException("La unidad es obligatoria");
        }
        try {
            String nombre = unidad.trim().toUpperCase(Locale.ROOT);
            String normalizado = switch (nombre) {
                case "MILIMETROS" -> "MILIMETRO";
                case "CENTIMETROS" -> "CENTIMETRO";
                case "METROS" -> "METRO";
                case "KILOMETROS" -> "KILOMETRO";
                case "YARDAS" -> "YARDA";
                case "MILIGRAMOS" -> "MILIGRAMO";
                case "GRAMOS" -> "GRAMO";
                case "KILOGRAMOS" -> "KILOGRAMO";
                case "TONELADAS" -> "TONELADA";
                case "ONZAS" -> "ONZA";
                default -> nombre;
            };
            return Enum.valueOf(tipo, normalizado);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unidad no soportada: " + unidad, ex);
        }
    }
}
