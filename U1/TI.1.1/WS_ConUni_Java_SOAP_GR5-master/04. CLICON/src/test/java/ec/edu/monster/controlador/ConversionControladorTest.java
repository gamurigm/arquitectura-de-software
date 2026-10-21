package ec.edu.monster.controlador;

import client.ws.ClienteWSConversor;
import ec.edu.monster.modelo.ConversionModelo;
import ec.edu.monster.modelo.TipoConversion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ConversionControladorTest {

    @Test
    void debeConvertirLongitudUsandoClienteWs() {
        ClienteWSConversor clienteWs = mock(ClienteWSConversor.class);
        ConversionControlador controlador = new ConversionControlador(clienteWs);
        ConversionModelo modelo = new ConversionModelo(TipoConversion.LONGITUD, 10.0, "METRO", "YARDA");

        when(clienteWs.convertirLongitud(10.0, "METRO", "YARDA")).thenReturn(10.936133);

        double resultado = controlador.convertir(modelo);

        assertEquals(10.936133, resultado, 0.000001);
        verify(clienteWs).convertirLongitud(10.0, "METRO", "YARDA");
        verifyNoMoreInteractions(clienteWs);
    }

    @Test
    void debeConvertirMasaUsandoClienteWs() {
        ClienteWSConversor clienteWs = mock(ClienteWSConversor.class);
        ConversionControlador controlador = new ConversionControlador(clienteWs);
        ConversionModelo modelo = new ConversionModelo(TipoConversion.MASA, 2.5, "KILOGRAMO", "ONZA");

        when(clienteWs.convertirMasa(2.5, "KILOGRAMO", "ONZA")).thenReturn(88.184904);

        double resultado = controlador.convertir(modelo);

        assertEquals(88.184904, resultado, 0.000001);
        verify(clienteWs).convertirMasa(2.5, "KILOGRAMO", "ONZA");
        verifyNoMoreInteractions(clienteWs);
    }

    @Test
    void debeConvertirTemperaturaUsandoClienteWs() {
        ClienteWSConversor clienteWs = mock(ClienteWSConversor.class);
        ConversionControlador controlador = new ConversionControlador(clienteWs);
        ConversionModelo modelo = new ConversionModelo(TipoConversion.TEMPERATURA, 25.0, "CELSIUS", "RANKINE");

        when(clienteWs.convertirTemperatura(25.0, "CELSIUS", "RANKINE")).thenReturn(536.67);

        double resultado = controlador.convertir(modelo);

        assertEquals(536.67, resultado, 0.000001);
        verify(clienteWs).convertirTemperatura(25.0, "CELSIUS", "RANKINE");
        verifyNoMoreInteractions(clienteWs);
    }

    @Test
    void debeFallarCuandoSolicitudEsNula() {
        ClienteWSConversor clienteWs = mock(ClienteWSConversor.class);
        ConversionControlador controlador = new ConversionControlador(clienteWs);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controlador.convertir(null)
        );

        assertEquals("La solicitud de conversion es obligatoria.", ex.getMessage());
        verifyNoInteractions(clienteWs);
    }

    @Test
    void debeFallarCuandoTipoEsNulo() {
        ClienteWSConversor clienteWs = mock(ClienteWSConversor.class);
        ConversionControlador controlador = new ConversionControlador(clienteWs);
        ConversionModelo modelo = new ConversionModelo(null, 10.0, "METRO", "YARDA");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> controlador.convertir(modelo)
        );

        assertEquals("Debe seleccionar el tipo de conversion.", ex.getMessage());
        verifyNoInteractions(clienteWs);
    }

    @Test
    void debeFallarCuandoUnidadInicialEsNulaOBlanca() {
        ClienteWSConversor clienteWs = mock(ClienteWSConversor.class);
        ConversionControlador controlador = new ConversionControlador(clienteWs);

        ConversionModelo nulo = new ConversionModelo(TipoConversion.LONGITUD, 10.0, null, "YARDA");
        IllegalArgumentException exNulo = assertThrows(
                IllegalArgumentException.class,
                () -> controlador.convertir(nulo)
        );
        assertEquals("La unidad inicial es obligatoria.", exNulo.getMessage());

        ConversionModelo blanco = new ConversionModelo(TipoConversion.LONGITUD, 10.0, "   ", "YARDA");
        IllegalArgumentException exBlanco = assertThrows(
                IllegalArgumentException.class,
                () -> controlador.convertir(blanco)
        );
        assertEquals("La unidad inicial es obligatoria.", exBlanco.getMessage());

        verifyNoInteractions(clienteWs);
    }

    @Test
    void debeFallarCuandoUnidadFinalEsNulaOBlanca() {
        ClienteWSConversor clienteWs = mock(ClienteWSConversor.class);
        ConversionControlador controlador = new ConversionControlador(clienteWs);

        ConversionModelo nulo = new ConversionModelo(TipoConversion.LONGITUD, 10.0, "METRO", null);
        IllegalArgumentException exNulo = assertThrows(
                IllegalArgumentException.class,
                () -> controlador.convertir(nulo)
        );
        assertEquals("La unidad final es obligatoria.", exNulo.getMessage());

        ConversionModelo blanco = new ConversionModelo(TipoConversion.LONGITUD, 10.0, "METRO", "   ");
        IllegalArgumentException exBlanco = assertThrows(
                IllegalArgumentException.class,
                () -> controlador.convertir(blanco)
        );
        assertEquals("La unidad final es obligatoria.", exBlanco.getMessage());

        verifyNoInteractions(clienteWs);
    }
}
