package ec.edu.monster.conversion.service;

import ec.edu.monster.conversion.dto.ConversionRequest;
import ec.edu.monster.conversion.dto.ConversionResponse;
import ec.edu.monster.conversion.exception.UnidadInvalidaException;
import ec.edu.monster.conversion.model.Magnitud;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversionServiceTest {

    private final ConversionService service = new ConversionService(new UnidadCatalog());

    @Test
    void convierteMetrosAPies() {
        ConversionResponse r = service.convertir(Magnitud.LONGITUD,
                new ConversionRequest(10.0, "metros", "pies"));
        assertEquals(32.808399, r.resultado(), 0.001);
        assertTrue(r.exito());
    }

    @Test
    void convierteKilogramosALibras() {
        ConversionResponse r = service.convertir(Magnitud.MASA,
                new ConversionRequest(1.0, "kilogramos", "libras"));
        assertEquals(2.204623, r.resultado(), 0.001);
    }

    @Test
    void convierteCelsiusAFahrenheit() {
        ConversionResponse r = service.convertir(Magnitud.TEMPERATURA,
                new ConversionRequest(100.0, "celsius", "fahrenheit"));
        assertEquals(212.0, r.resultado(), 0.0001);
    }

    @Test
    void convierteCelsiusAKelvin() {
        ConversionResponse r = service.convertir(Magnitud.TEMPERATURA,
                new ConversionRequest(0.0, "celsius", "kelvin"));
        assertEquals(273.15, r.resultado(), 0.0001);
    }

    @Test
    void unidadInvalidaLanzaExcepcion() {
        assertThrows(UnidadInvalidaException.class, () ->
                service.convertir(Magnitud.LONGITUD, new ConversionRequest(1.0, "metros", "leguas")));
    }
}
