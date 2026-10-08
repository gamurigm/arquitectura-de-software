package ec.edu.monster.modelos.utilidades.mapeadores;

import ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;
import ec.edu.monster.modelos.utilidades.enums.UnidadMasa;
import ec.edu.monster.modelos.utilidades.enums.UnidadLongitud;

public class UnidadMapper {

    public static UnidadLongitud toLongitud(String unidad) {
        try {
            return UnidadLongitud.valueOf(unidad.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unidad de longitud inválida: " + unidad);
        }
    }

    public static UnidadMasa toMasa(String unidad) {
        try {
            return UnidadMasa.valueOf(unidad.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unidad de masa inválida: " + unidad);
        }
    }

    public static UnidadTemperatura toTemperatura(String unidad) {
        try {
            return UnidadTemperatura.valueOf(unidad.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unidad de temperatura inválida: " + unidad);
        }
    }
}