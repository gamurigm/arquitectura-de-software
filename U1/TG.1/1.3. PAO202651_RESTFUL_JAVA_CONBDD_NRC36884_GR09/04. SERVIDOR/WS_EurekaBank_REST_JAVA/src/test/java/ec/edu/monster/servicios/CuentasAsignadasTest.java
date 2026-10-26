package ec.edu.monster.servicios;

import ec.edu.monster.db.RepositorioEurekaBank;
import ec.edu.monster.modelo.CuentaDto;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.SolicitudTransferencia;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

final class CuentasAsignadasTest {
    @Test void rechazaInicioDeSesionSinClienteAsociado() throws SQLException {
        EurekaBankService servicio = new EurekaBankService(new RepositorioPrueba(null));
        assertNull(servicio.iniciarSesion("cliente", "clave"));
    }

    @Test void devuelveCuentasDelClienteDeLaSesion() throws SQLException {
        RepositorioPrueba repositorio = new RepositorioPrueba("00005");
        EurekaBankService servicio = new EurekaBankService(repositorio);
        String token = servicio.iniciarSesion("cliente", "clave");

        List<CuentaDto> cuentas = servicio.traerCuentas(token);

        assertEquals("00005", repositorio.codigoConsultado);
        assertEquals("00100001", cuentas.get(0).getNumero());
    }

    private static final class RepositorioPrueba implements RepositorioEurekaBank {
        private final String codigoCliente;
        private String codigoConsultado;
        private RepositorioPrueba(String codigoCliente) { this.codigoCliente = codigoCliente; }
        public boolean autenticar(String usuario, char[] contrasena) { return "clave".equals(new String(contrasena)); }
        public String obtenerCodigoCliente(String usuario) { return codigoCliente; }
        public List<CuentaDto> traerCuentas(String codigoCliente) { codigoConsultado = codigoCliente; return List.of(new CuentaDto("00100001", "01", "Soles", new BigDecimal("100.00"))); }
        public List<Movimiento> traerMovimientos(String cuenta, String codigoCliente) { throw new UnsupportedOperationException(); }
        public boolean registrarDeposito(String cuenta, BigDecimal importe, String codigoCliente) { throw new UnsupportedOperationException(); }
        public boolean transferir(String usuario, String codigoCliente, SolicitudTransferencia solicitud) { throw new UnsupportedOperationException(); }
    }
}
