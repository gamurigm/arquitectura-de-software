package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enumeraciones.UnidadMasa;
import java.util.Map;

public class ConversorMasa implements IConversor<UnidadMasa> {

    private static final Map<UnidadMasa, Double> FACTORES = Map.of(
            UnidadMasa.MILIGRAMO, 0.001,
            UnidadMasa.GRAMO, 1.0,
            UnidadMasa.KILOGRAMO, 1000.0,
            UnidadMasa.TONELADA, 1_000_000.0,
            UnidadMasa.ONZA, 28.349523125,
            UnidadMasa.LIBRAS, 453.59237,
            UnidadMasa.QUINTALES, 100_000.0);

    @Override
    public double convertir(double valor, UnidadMasa unidadInicial, UnidadMasa unidadFinal) {
        if (unidadInicial == null || unidadFinal == null) {
            throw new IllegalArgumentException("Las unidades no pueden ser nulas");
        }
        return valor * FACTORES.get(unidadInicial) / FACTORES.get(unidadFinal);
    }
}
