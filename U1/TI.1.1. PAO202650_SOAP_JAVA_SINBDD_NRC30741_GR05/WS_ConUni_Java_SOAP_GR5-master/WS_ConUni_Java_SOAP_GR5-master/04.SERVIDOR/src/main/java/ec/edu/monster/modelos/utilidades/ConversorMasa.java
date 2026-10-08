package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enums.UnidadMasa;
import java.util.Map;

public class ConversorMasa implements IConversor<UnidadMasa> {

    private final Map<UnidadMasa, Double> factores = Map.of(
        UnidadMasa.MILIGRAMO, 0.001,
        UnidadMasa.GRAMO, 1.0,
        UnidadMasa.KILOGRAMO, 1000.0,
        UnidadMasa.TONELADA, 1_000_000.0,
        UnidadMasa.ONZA, 28.3495
    );

    public ConversorMasa() {}

    @Override
    public double convertir(double valor, UnidadMasa unidadOrigen, UnidadMasa unidadFinal) {

        if (unidadOrigen == null || unidadFinal == null) {
            throw new IllegalArgumentException("Las unidades no pueden ser null");
        }
        if (!factores.containsKey(unidadOrigen) || !factores.containsKey(unidadFinal)) {
            throw new IllegalArgumentException("Unidad no soportada");
        }

        double valorEnBase = valor * factores.get(unidadOrigen);

        return valorEnBase / factores.get(unidadFinal);
    }
}