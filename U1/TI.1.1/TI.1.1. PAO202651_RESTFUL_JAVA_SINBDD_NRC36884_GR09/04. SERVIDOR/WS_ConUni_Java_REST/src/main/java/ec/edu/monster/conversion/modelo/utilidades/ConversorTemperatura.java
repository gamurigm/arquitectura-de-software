package ec.edu.monster.conversion.modelo.utilidades;

import ec.edu.monster.conversion.modelo.UnidadTemperatura;
import org.springframework.stereotype.Service;

@Service
public class ConversorTemperatura implements IConversor<UnidadTemperatura> {

    @Override
    public double convertir(double valor, UnidadTemperatura origen, UnidadTemperatura destino) {
        return desdeCelsius(destino, aCelsius(origen, valor));
    }

    private double aCelsius(UnidadTemperatura unidad, double valor) {
        return switch (unidad) {
            case CELSIUS -> valor;
            case FAHRENHEIT -> (valor - 32.0) * 5.0 / 9.0;
            case KELVIN -> valor - 273.15;
            case NEWTON -> valor * 100.0 / 33.0;
            case REAUMUR -> valor * 5.0 / 4.0;
        };
    }

    private double desdeCelsius(UnidadTemperatura unidad, double celsius) {
        return switch (unidad) {
            case CELSIUS -> celsius;
            case FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0;
            case KELVIN -> celsius + 273.15;
            case NEWTON -> celsius * 33.0 / 100.0;
            case REAUMUR -> celsius * 4.0 / 5.0;
        };
    }
}
