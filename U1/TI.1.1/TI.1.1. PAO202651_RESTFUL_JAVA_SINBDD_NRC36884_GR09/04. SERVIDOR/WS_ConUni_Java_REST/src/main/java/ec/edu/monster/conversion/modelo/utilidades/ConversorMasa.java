package ec.edu.monster.conversion.modelo.utilidades;

import ec.edu.monster.conversion.modelo.UnidadMasa;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ConversorMasa implements IConversor<UnidadMasa> {

    private static final Map<UnidadMasa, Double> FACTORES = Map.of(
            UnidadMasa.GRAMOS, 0.001,
            UnidadMasa.KILOGRAMOS, 1.0,
            UnidadMasa.ONZAS, 0.028349523125,
            UnidadMasa.LIBRAS, 0.45359237,
            UnidadMasa.QUINTALES, 100.0);

    @Override
    public double convertir(double valor, UnidadMasa unidadOrigen, UnidadMasa unidadDestino) {
        return valor * FACTORES.get(unidadOrigen) / FACTORES.get(unidadDestino);
    }
}
