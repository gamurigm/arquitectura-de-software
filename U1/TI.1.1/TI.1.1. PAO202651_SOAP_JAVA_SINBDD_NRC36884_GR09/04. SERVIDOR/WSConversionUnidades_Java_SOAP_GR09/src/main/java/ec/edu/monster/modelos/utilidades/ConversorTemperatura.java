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

        return switch (origen) {
            case CELSIUS -> switch (destino) {
                case FAHRENHEIT -> valor * 9 / 5 + 32;
                case KELVIN -> valor + 273.15;
                case RANKINE -> (valor + 273.15) * 9 / 5;
                case CELSIUS -> valor;
            };
            case FAHRENHEIT -> switch (destino) {
                case CELSIUS -> (valor - 32) * 5 / 9;
                case KELVIN -> (valor - 32) * 5 / 9 + 273.15;
                case RANKINE -> valor + 459.67;
                case FAHRENHEIT -> valor;
            };
            case KELVIN -> switch (destino) {
                case CELSIUS -> valor - 273.15;
                case FAHRENHEIT -> (valor - 273.15) * 9 / 5 + 32;
                case RANKINE -> valor * 9 / 5;
                case KELVIN -> valor;
            };
            case RANKINE -> switch (destino) {
                case CELSIUS -> (valor - 491.67) * 5 / 9;
                case FAHRENHEIT -> valor - 459.67;
                case KELVIN -> valor * 5 / 9;
                case RANKINE -> valor;
            };
        };
    }
}
