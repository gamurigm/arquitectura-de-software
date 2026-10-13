package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enumeraciones.UnidadLongitud;
import java.util.Map;

public class ConversorLongitud implements IConversor<UnidadLongitud> {

    private static final Map<UnidadLongitud, Double> FACTORES = Map.of(
            UnidadLongitud.MILIMETRO, 0.001,
            UnidadLongitud.CENTIMETRO, 0.01,
            UnidadLongitud.METRO, 1.0,
            UnidadLongitud.KILOMETRO, 1000.0,
            UnidadLongitud.YARDA, 0.9144);

    @Override
    public double convertir(double valor, UnidadLongitud unidadInicial, UnidadLongitud unidadFinal) {
        if (unidadInicial == null || unidadFinal == null) {
            throw new IllegalArgumentException("Las unidades no pueden ser nulas");
        }
        return valor * FACTORES.get(unidadInicial) / FACTORES.get(unidadFinal);
    }
}
