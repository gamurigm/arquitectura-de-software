package ec.edu.monster.modelos.utilidades.mapeadores;

import ec.edu.monster.modelos.utilidades.enums.UnidadLongitud;
import ec.edu.monster.modelos.utilidades.enums.UnidadMasa;
import ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;
import java.util.Locale;

public final class UnidadMapper {

    private UnidadMapper() {
    }

    public static UnidadLongitud toLongitud(String unidad) {
        return parsear(unidad, UnidadLongitud.class);
    }

    public static UnidadMasa toMasa(String unidad) {
        return parsear(unidad, UnidadMasa.class);
    }

    public static UnidadTemperatura toTemperatura(String unidad) {
        return parsear(unidad, UnidadTemperatura.class);
    }

    private static <T extends Enum<T>> T parsear(String unidad, Class<T> tipo) {
        if (unidad == null || unidad.isBlank()) {
            throw new IllegalArgumentException("La unidad es obligatoria");
        }
        try {
            return Enum.valueOf(tipo, unidad.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unidad no soportada: " + unidad, ex);
        }
    }
}