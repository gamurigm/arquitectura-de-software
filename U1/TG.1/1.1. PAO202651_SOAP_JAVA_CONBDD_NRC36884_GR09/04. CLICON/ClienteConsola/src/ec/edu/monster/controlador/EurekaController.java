package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.service.EurekaService;
import java.math.BigDecimal;
import java.util.List;

public final class EurekaController {
    private final EurekaService servicio;
    private String token;
    private TransferenciaPendiente pendiente;
    private List<Cuenta> cuentas = List.of();

    public EurekaController(EurekaService servicio) {
        this.servicio = servicio;
    }

    public List<Movimiento> consultarMovimientos(String cuenta) throws java.io.IOException {
        exigirSesion();
        try { return servicio.traerMovimientos(token, cuenta); }
        catch (java.io.IOException e) { expirar(e); throw e; }
    }

    public void iniciarSesion(String token) throws java.io.IOException {
        this.token = token;
        try { cuentas = servicio.traerCuentas(token); }
        catch (java.io.IOException e) {
            this.token = null;
            try { servicio.cerrarSesion(token); } catch (java.io.IOException cierre) { e.addSuppressed(cierre); }
            throw e;
        }
    }
    public List<Cuenta> cuentas() { return cuentas; }

    public boolean transferir(String origen, String destino, BigDecimal importe) throws java.io.IOException {
        exigirSesion();
        if (pendiente == null || !pendiente.coincide(origen, destino, importe))
            pendiente = new TransferenciaPendiente(origen, destino, importe, java.util.UUID.randomUUID());
        boolean resultado;
        try { resultado = servicio.transferir(token, origen, destino, importe, pendiente.clave()); }
        catch (java.io.IOException e) { expirar(e); throw e; }
        pendiente = null;
        return resultado;
    }

    public void cerrarSesion() throws java.io.IOException {
        String actual = token; token = null; pendiente = null; cuentas = List.of();
        if (actual != null) servicio.cerrarSesion(actual);
    }

    public boolean registrarDeposito(String cuenta, BigDecimal importe) throws java.io.IOException {
        exigirSesion();
        try { return servicio.regDeposito(token, cuenta, importe) == 1; }
        catch (java.io.IOException e) { expirar(e); throw e; }
    }

    private void exigirSesion() { if (token == null) throw new IllegalStateException("Inicie sesión para continuar."); }
    private void expirar(java.io.IOException error) { if (error.getMessage() != null && error.getMessage().contains("Token ausente, inválido o caducado")) { token = null; pendiente = null; } }

    private record TransferenciaPendiente(String origen, String destino, BigDecimal importe, java.util.UUID clave) {
        boolean coincide(String o, String d, BigDecimal i) { return origen.equals(o) && destino.equals(d) && importe.compareTo(i) == 0; }
    }
}
