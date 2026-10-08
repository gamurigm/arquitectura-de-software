package ec.edu.monster.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TipoConversionTest {

    @Test
    void debeExponerNombreVisibleParaCadaTipo() {
        assertEquals("Longitud", TipoConversion.LONGITUD.getNombreVisible());
        assertEquals("Masa", TipoConversion.MASA.getNombreVisible());
        assertEquals("Temperatura", TipoConversion.TEMPERATURA.getNombreVisible());
    }

    @Test
    void debeMantenerOrdenEsperadoDeValores() {
        assertArrayEquals(
                new TipoConversion[]{TipoConversion.LONGITUD, TipoConversion.MASA, TipoConversion.TEMPERATURA},
                TipoConversion.values()
        );
    }

    @Test
    void toStringDebeRetornarNombreDelEnum() {
        assertEquals("LONGITUD", TipoConversion.LONGITUD.toString());
        assertEquals("MASA", TipoConversion.MASA.toString());
        assertEquals("TEMPERATURA", TipoConversion.TEMPERATURA.toString());
    }
}
