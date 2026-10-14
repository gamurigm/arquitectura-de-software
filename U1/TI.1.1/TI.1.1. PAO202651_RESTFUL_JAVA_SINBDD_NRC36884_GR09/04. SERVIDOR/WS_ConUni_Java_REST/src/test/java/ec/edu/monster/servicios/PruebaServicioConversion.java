package ec.edu.monster.servicios;

import ec.edu.monster.modelo.dto.SolicitudConversion;
import ec.edu.monster.modelo.dto.RespuestaConversion;
import ec.edu.monster.excepciones.ExcepcionUnidadInvalida;
import ec.edu.monster.modelo.Magnitud;
import ec.edu.monster.modelo.utilidades.ConversorLongitud;
import ec.edu.monster.modelo.utilidades.ConversorMasa;
import ec.edu.monster.modelo.utilidades.ConversorTemperatura;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PruebaServicioConversion {

    private final CatalogoUnidades catalogo = new CatalogoUnidades();
    private final ServicioConversion servicio = new ServicioConversion(catalogo,
            new ConversorLongitud(), new ConversorMasa(), new ConversorTemperatura());

    @Test
    void convierteMetrosAPies() {
        RespuestaConversion r = servicio.convertir(Magnitud.LONGITUD,
                new SolicitudConversion(10.0, "metros", "pies"));
        assertEquals(32.808399, r.resultado(), 0.001);
        assertTrue(r.exito());
    }

    @Test
    void convierteKilogramosALibras() {
        RespuestaConversion r = servicio.convertir(Magnitud.MASA,
                new SolicitudConversion(1.0, "kilogramos", "libras"));
        assertEquals(2.204623, r.resultado(), 0.001);
    }

    @Test
    void convierteCelsiusAFahrenheit() {
        RespuestaConversion r = servicio.convertir(Magnitud.TEMPERATURA,
                new SolicitudConversion(100.0, "celsius", "fahrenheit"));
        assertEquals(212.0, r.resultado(), 0.0001);
    }

    @Test
    void convierteCelsiusAKelvin() {
        RespuestaConversion r = servicio.convertir(Magnitud.TEMPERATURA,
                new SolicitudConversion(0.0, "celsius", "kelvin"));
        assertEquals(273.15, r.resultado(), 0.0001);
    }

    @Test
    void unidadInvalidaLanzaExcepcion() {
        assertThrows(ExcepcionUnidadInvalida.class, () ->
                servicio.convertir(Magnitud.LONGITUD, new SolicitudConversion(1.0, "metros", "leguas")));
    }
}
