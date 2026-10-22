package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.service.EurekaService;
import java.math.BigDecimal;
import java.util.List;

public final class EurekaController {
    private final EurekaService servicio;
    private String token;
    private TransferenciaPendiente pendiente;

    public EurekaController(EurekaService servicio) {
        this.servicio = servicio;
    }

    public List<Movimiento> consultarMovimientos(String cuenta) {
        return servicio.traerMovimientos(cuenta);
    }

    public void iniciarSesion(String token) { this.token = token; }

    public boolean transferir(String origen, String destino, BigDecimal importe) throws java.io.IOException {
        if (token == null) throw new IllegalStateException("Inicie sesión para continuar.");
        if (pendiente == null || !pendiente.coincide(origen, destino, importe))
            pendiente = new TransferenciaPendiente(origen, destino, importe, java.util.UUID.randomUUID());
        boolean resultado = servicio.transferir(token, origen, destino, importe, pendiente.clave());
        pendiente = null;
        return resultado;
    }

    public void cerrarSesion() throws java.io.IOException {
        String actual = token; token = null; pendiente = null;
        if (actual != null) servicio.cerrarSesion(actual);
    }

    public boolean registrarDeposito(String cuenta, BigDecimal importe) {
        return servicio.regDeposito(cuenta, importe) == 1;
    }

    private record TransferenciaPendiente(String origen, String destino, BigDecimal importe, java.util.UUID clave) {
        boolean coincide(String o, String d, BigDecimal i) { return origen.equals(o) && destino.equals(d) && importe.compareTo(i) == 0; }
    }
}
