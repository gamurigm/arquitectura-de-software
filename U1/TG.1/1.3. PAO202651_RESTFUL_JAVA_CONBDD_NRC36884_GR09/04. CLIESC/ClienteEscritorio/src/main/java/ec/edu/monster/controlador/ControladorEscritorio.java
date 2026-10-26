package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.servicios.ClienteEurekaRest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public final class ControladorEscritorio {
    private final ClienteEurekaRest cliente;
    private String usuario;
    private String token;
    private TransferenciaPendiente transferenciaPendiente;
    private List<Cuenta> cuentas = List.of();

    public ControladorEscritorio(ClienteEurekaRest cliente) { this.cliente = cliente; }
    public boolean iniciarSesion(String usuario, String contrasena) throws IOException, InterruptedException {
        String token = cliente.iniciarSesion(usuario, contrasena);
        if (token == null || token.isBlank()) return false;
        this.usuario = usuario;
        this.token = token;
        try { cuentas = cliente.traerCuentas(token); }
        catch (IOException | InterruptedException e) { this.token = null; this.usuario = null; if (e instanceof InterruptedException) Thread.currentThread().interrupt(); throw e; }
        return true;
    }
    public List<Cuenta> cuentas() { return cuentas; }
    public String usuarioActual() { return usuario; }
    public boolean estaAutenticado() { return usuario != null && token != null; }
    public void cerrarSesion() throws IOException, InterruptedException {
        String actual = token; token = null; usuario = null; cuentas = List.of();
        if (actual != null) cliente.cerrarSesion(actual);
    }
    public List<Movimiento> consultarMovimientos(String cuenta) throws IOException, InterruptedException { exigirSesion(); try { return cliente.traerMovimientos(token, cuenta); } catch (IOException e) { expirarSiCorresponde(e); throw e; } }
    public boolean registrarDeposito(String cuenta, BigDecimal importe) throws IOException, InterruptedException { exigirSesion(); try { return cliente.registrarDeposito(token, cuenta, importe); } catch (IOException e) { expirarSiCorresponde(e); throw e; } }
    public boolean transferir(String origen, String destino, BigDecimal importe) throws IOException, InterruptedException {
        exigirSesion();
        if (transferenciaPendiente == null || !transferenciaPendiente.coincide(origen, destino, importe))
            transferenciaPendiente = new TransferenciaPendiente(origen, destino, importe, java.util.UUID.randomUUID().toString());
        boolean resultado;
        try { resultado = cliente.transferir(token, origen, destino, importe, transferenciaPendiente.clave()); }
        catch (IOException e) { expirarSiCorresponde(e); throw e; }
        transferenciaPendiente = null;
        return resultado;
    }
    private void exigirSesion() { if (!estaAutenticado()) throw new IllegalStateException("Inicie sesión para continuar."); }
    private void expirarSiCorresponde(IOException error) { if (error.getMessage() != null && error.getMessage().contains("HTTP 401")) { token = null; usuario = null; cuentas = List.of(); transferenciaPendiente = null; } }
    private record TransferenciaPendiente(String origen, String destino, BigDecimal importe, String clave) {
        boolean coincide(String o, String d, BigDecimal i) { return origen.equals(o) && destino.equals(d) && importe.compareTo(i) == 0; }
    }
}
