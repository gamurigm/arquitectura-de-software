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
            return Enum.valueOf(tipo, unidad.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unidad no soportada: " + unidad, ex);
        }
    }
}
