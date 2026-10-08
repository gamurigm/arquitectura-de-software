package ec.edu.monster.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConversionModeloTest {

    @Test
    void debeCrearModeloConConstructorCompleto() {
        ConversionModelo modelo = new ConversionModelo(
                TipoConversion.LONGITUD,
                15.5,
                "METRO",
                "YARDA"
        );

        assertEquals(TipoConversion.LONGITUD, modelo.getTipoConversion());
        assertEquals(15.5, modelo.getValor(), 0.000001);
        assertEquals("METRO", modelo.getUnidadInicial());
        assertEquals("YARDA", modelo.getUnidadFinal());
    }

    @Test
    void debeActualizarCamposConSetters() {
        ConversionModelo modelo = new ConversionModelo();

        modelo.setTipoConversion(TipoConversion.TEMPERATURA);
        modelo.setValor(100.0);
        modelo.setUnidadInicial("CELSIUS");
        modelo.setUnidadFinal("RANKINE");

        assertEquals(TipoConversion.TEMPERATURA, modelo.getTipoConversion());
        assertEquals(100.0, modelo.getValor(), 0.000001);
        assertEquals("CELSIUS", modelo.getUnidadInicial());
        assertEquals("RANKINE", modelo.getUnidadFinal());
    }
}
