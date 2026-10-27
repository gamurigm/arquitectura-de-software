package ec.edu.monster.controlador;

import ec.edu.monster.SoapSimulado;
import ec.edu.monster.service.EurekaService;
import jakarta.xml.ws.WebServiceException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class EurekaControladorTest {
    private SoapSimulado banco;
    private EurekaControlador controlador;

    @BeforeEach
    void preparar() throws Exception {
        banco = new SoapSimulado();
        controlador = new EurekaControlador(new EurekaService(banco.endpoint()));
    }

    @AfterEach
    void cerrar() {
        banco.close();
    }

    private void autenticar() {
        assertTrue(controlador.iniciarSesion("usuario-prueba", "clave-prueba".toCharArray()));
        banco.llamadas.set(0);
    }

    @Test
    void protegeOperacionesAntesDeAutenticar() {
        assertThrows(IllegalStateException.class, () -> controlador.consultarMovimientos("00100001"));
        assertThrows(IllegalStateException.class, () -> controlador.registrarDeposito("00100001", "1.20"));
        assertEquals(0, banco.llamadas.get());
    }

    @Test
    void conservaEspaciosDeLaClaveYBorraElArreglo() {
        char[] clave = " clave-prueba ".toCharArray();
        assertTrue(controlador.iniciarSesion(" usuario-prueba ", clave));
        assertEquals(" clave-prueba ", banco.ultimaClave);
        assertArrayEquals(new char[clave.length], clave);
    }

    @Test
    void loginRechazadoInvalidaLaSesionPrevia() {
        autenticar();
        banco.resultadoLogin = -1;
        assertFalse(controlador.iniciarSesion("otro", "incorrecta".toCharArray()));
        assertThrows(IllegalStateException.class, () -> controlador.consultarMovimientos("00100001"));
    }

    @Test
    void rechazaCamposDeLoginVaciosSinPeticiones() {
        assertThrows(IllegalArgumentException.class,
                () -> controlador.iniciarSesion(" ", "clave".toCharArray()));
        assertThrows(IllegalArgumentException.class,
                () -> controlador.iniciarSesion("usuario", new char[0]));
        assertEquals(0, banco.llamadas.get());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "123456789", "ABC12345"})
    void rechazaCuentasInvalidas(String cuenta) {
        autenticar();
        assertThrows(IllegalArgumentException.class, () -> controlador.consultarMovimientos(cuenta));
        assertThrows(IllegalArgumentException.class, () -> controlador.registrarDeposito(cuenta, "1.20"));
        assertEquals(0, banco.llamadas.get());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "0", "-1", "1.234", "NaN", "1e3", "10000000000", "1,2.3"})
    void rechazaImportesInvalidosSinPeticiones(String importe) {
        autenticar();
        assertThrows(IllegalArgumentException.class,
                () -> controlador.registrarDeposito("00100001", importe));
        assertEquals(0, banco.llamadas.get());
    }

    @Test
    void enviaDepositoDecimalExactoConCuentaSinPerderCeros() {
        autenticar();
        assertTrue(controlador.registrarDeposito(" 00100001 ", "1234567890,19"));
        assertEquals(new BigDecimal("1234567890.19"), banco.ultimoImporte);
        assertEquals("00100001", banco.ultimaCuenta);
        assertEquals(1, banco.llamadas.get());
    }

    @Test
    void restringeOrigenAlListadoDeCuentasDeLaSesion() {
        autenticar();
        assertEquals(2, controlador.cuentas().size());
        assertThrows(IllegalArgumentException.class,
                () -> controlador.transferir("00999999", "00100002", "1.00"));
        assertEquals(0, banco.llamadas.get());
    }

    @Test
    void noInformaExitoCuandoElServidorRechazaDeposito() {
        autenticar();
        banco.resultadoDeposito = -1;
        assertFalse(controlador.registrarDeposito("00100001", "1.20"));
    }

    @Test
    void validaTransferenciaAntesDeEnviar() {
        autenticar();
        assertThrows(IllegalArgumentException.class,
                () -> controlador.transferir("0010000", "00100002", "1.00"));
        assertThrows(IllegalArgumentException.class,
                () -> controlador.transferir("00100001", "00100001", "1.00"));
        assertThrows(IllegalArgumentException.class,
                () -> controlador.transferir("00100001", "00100002", "1.001"));
        assertEquals(0, banco.llamadas.get());
    }

    @Test
    void enviaTransferenciaConDosCuentasYClaveIdempotente() {
        autenticar();
        assertTrue(controlador.transferir("00100001", "00100002", "23.45"));
        assertEquals("00100001", banco.ultimaCuentaOrigen);
        assertEquals("00100002", banco.ultimaCuentaDestino);
        assertEquals(new BigDecimal("23.45"), banco.ultimoImporte);
        assertDoesNotThrow(() -> UUID.fromString(banco.ultimaClaveOperacion));
        assertEquals(1, banco.llamadas.get());
    }

    @Test
    void conservaClaveIdempotenteTrasRespuestaIncierta() {
        autenticar();
        banco.fallar = true;
        assertThrows(IllegalStateException.class,
                () -> controlador.transferir("00100001", "00100002", "23.45"));
        String clave = banco.ultimaClaveOperacion;
        assertDoesNotThrow(() -> UUID.fromString(clave));
        banco.fallar = false;
        assertTrue(controlador.transferir("00100001", "00100002", "23.45"));
        assertEquals(clave, banco.ultimaClaveOperacion);
        assertEquals(2, banco.llamadas.get());
    }

    @Test
    void convierteMovimientosSinPerderDecimalesNiFecha() {
        autenticar();
        var movimientos = controlador.consultarMovimientos("00100001");
        assertEquals(1, movimientos.size());
        assertEquals(new BigDecimal("1234567890.19"), movimientos.getFirst().importe());
        assertEquals("2026-10-21T10:30", movimientos.getFirst().fecha().toString());
    }

    @Test
    void propagaFalloSoapSinReintentarDeposito() {
        autenticar();
        banco.fallar = true;
        assertThrows(WebServiceException.class,
                () -> controlador.registrarDeposito("00100001", "1.20"));
        assertEquals(1, banco.llamadas.get());
    }

    @Test
    void cerrarSesionImpidePeticionesPosteriores() {
        autenticar();
        controlador.cerrarSesion();
        assertThrows(IllegalStateException.class, () -> controlador.consultarMovimientos("00100001"));
        assertEquals(1, banco.llamadas.get());
        assertEquals("token-prueba", banco.ultimoTokenRevocado);
    }
}
