package ec.edu.monster.controlador;

import ec.edu.monster.cliente.soap.ClienteWSConversor;
import ec.edu.monster.modelo.ConversionModelo;
import ec.edu.monster.modelo.TipoConversion;

public class ConversionControlador {

    private final ClienteWSConversor clienteWs;

    public ConversionControlador(ClienteWSConversor clienteWs) {
        this.clienteWs = clienteWs;
    }

    public double convertir(ConversionModelo modelo) {
        validarSolicitud(modelo);

        TipoConversion tipo = modelo.obtenerTipoConversion();

        return switch (tipo) {
            case LONGITUD -> clienteWs.convertirLongitud(
                    modelo.obtenerValor(),
                    modelo.obtenerUnidadInicial(),
                    modelo.obtenerUnidadFinal());
            case MASA -> clienteWs.convertirMasa(
                    modelo.obtenerValor(),
                    modelo.obtenerUnidadInicial(),
                    modelo.obtenerUnidadFinal());
            case TEMPERATURA -> clienteWs.convertirTemperatura(
                    modelo.obtenerValor(),
                    modelo.obtenerUnidadInicial(),
                    modelo.obtenerUnidadFinal());
        };
    }

    private void validarSolicitud(ConversionModelo modelo) {
        if (modelo == null) {
            throw new IllegalArgumentException("La solicitud de conversion es obligatoria.");
        }
        if (modelo.obtenerTipoConversion() == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de conversion.");
        }
        if (modelo.obtenerUnidadInicial() == null || modelo.obtenerUnidadInicial().isBlank()) {
            throw new IllegalArgumentException("La unidad inicial es obligatoria.");
        }
        if (modelo.obtenerUnidadFinal() == null || modelo.obtenerUnidadFinal().isBlank()) {
            throw new IllegalArgumentException("La unidad final es obligatoria.");
        }
    }
}

