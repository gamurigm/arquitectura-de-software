package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.servicios.ClienteEurekaRest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public final class ControladorConsola {
    private final ClienteEurekaRest cliente;
    private String usuario;
    private String token;
    private List<Cuenta> cuentas = List.of();

    public ControladorConsola(ClienteEurekaRest cliente) { this.cliente = cliente; }

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

    public boolean estaAutenticado() { return usuario != null && token != null; }
    public String usuarioActual() { return usuario; }
    public void cerrarSesion() throws IOException, InterruptedException {
        String actual = token;
        token = null;
        usuario = null;
        cuentas = List.of();
        if (actual != null) cliente.cerrarSesion(actual);
    }

    public List<Movimiento> consultarMovimientos(String cuenta) throws IOException, InterruptedException {
        exigirSesion();
        try { return cliente.traerMovimientos(token, cuenta); }
        catch (IOException e) { expirarSiCorresponde(e); throw e; }
    }

    public boolean depositar(String cuenta, BigDecimal importe) throws IOException, InterruptedException {
        exigirSesion();
        try { return cliente.registrarDeposito(token, cuenta, importe); }
        catch (IOException e) { expirarSiCorresponde(e); throw e; }
    }

    public boolean transferir(String origen, String destino, BigDecimal importe, String claveOperacion)
            throws IOException, InterruptedException {
        exigirSesion();
        try { return cliente.transferir(token, origen, destino, importe, claveOperacion); }
        catch (IOException e) { expirarSiCorresponde(e); throw e; }
    }

    private void exigirSesion() {
        if (!estaAutenticado()) throw new IllegalStateException("Inicie sesión para continuar.");
    }

    private void expirarSiCorresponde(IOException error) {
        if (error.getMessage() != null && error.getMessage().contains("HTTP 401")) {
            token = null;
            usuario = null;
            cuentas = List.of();
        }
    }
}
