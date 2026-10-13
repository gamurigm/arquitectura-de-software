package ec.edu.monster.modelos.utilidades;

import ec.edu.monster.modelos.utilidades.enumeraciones.UnidadTemperatura;

public class ConversorTemperatura implements IConversor<UnidadTemperatura> {

    @Override
    public double convertir(double valor, UnidadTemperatura origen, UnidadTemperatura destino) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Las unidades no pueden ser nulas");
        }
        if (origen == destino) {
            return valor;
        }

        double celsius = switch (origen) {
            case CELSIUS -> valor;
            case FAHRENHEIT -> (valor - 32.0) * 5.0 / 9.0;
            case KELVIN -> valor - 273.15;
            case RANKINE -> valor * 5.0 / 9.0 - 273.15;
            case NEWTON -> valor * 100.0 / 33.0;
            case REAUMUR -> valor * 5.0 / 4.0;
        };
        return switch (destino) {
            case CELSIUS -> celsius;
            case FAHRENHEIT -> celsius * 9.0 / 5.0 + 32.0;
            case KELVIN -> celsius + 273.15;
            case RANKINE -> (celsius + 273.15) * 9.0 / 5.0;
            case NEWTON -> celsius * 33.0 / 100.0;
            case REAUMUR -> celsius * 4.0 / 5.0;
        };
    }
}
