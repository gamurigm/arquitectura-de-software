package ec.edu.monster.controladores.ws;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WSConversionUnidadesTest {

    private final WSConversionUnidades_Java_SOAP_GR09 servicio = new WSConversionUnidades_Java_SOAP_GR09();

    @Test
    void rechazaValoresNoFinitosEnConversiones() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.convertirLongitud(Double.NaN, "METRO", "YARDA"));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.convertirMasa(Double.POSITIVE_INFINITY, "GRAMO", "KILOGRAMO"));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.convertirTemperatura(Double.NEGATIVE_INFINITY, "CELSIUS", "KELVIN"));
    }

    @Test
    void rechazaMasaNegativaYConservaConversionValida() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.convertirMasa(-1, "GRAMO", "KILOGRAMO"));
        assertEquals(1.0, servicio.convertirMasa(1000, "GRAMO", "KILOGRAMO"), 0.000001);
    }
}
