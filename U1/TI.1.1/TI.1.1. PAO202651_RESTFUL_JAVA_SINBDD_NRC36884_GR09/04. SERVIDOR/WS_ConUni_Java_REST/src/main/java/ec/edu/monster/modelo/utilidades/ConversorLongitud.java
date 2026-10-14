package ec.edu.monster.modelo.utilidades;

import ec.edu.monster.modelo.UnidadLongitud;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConversorLongitud implements IConversor<UnidadLongitud> {

    private static final Map<UnidadLongitud, Double> FACTORES = Map.of(
            UnidadLongitud.MILIMETROS, 0.001,
            UnidadLongitud.CENTIMETROS, 0.01,
            UnidadLongitud.METROS, 1.0,
            UnidadLongitud.KILOMETROS, 1000.0,
            UnidadLongitud.PIES, 0.3048,
            UnidadLongitud.YARDAS, 0.9144,
            UnidadLongitud.MILLAS, 1609.344);

    @Override
    public double convertir(double valor, UnidadLongitud unidadOrigen, UnidadLongitud unidadDestino) {
        return valor * FACTORES.get(unidadOrigen) / FACTORES.get(unidadDestino);
    }
}
