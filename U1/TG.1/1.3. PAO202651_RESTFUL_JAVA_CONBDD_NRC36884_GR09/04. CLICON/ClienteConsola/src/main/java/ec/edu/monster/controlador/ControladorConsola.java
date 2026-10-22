package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.servicios.ClienteEurekaRest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public final class ControladorConsola {
    private final ClienteEurekaRest cliente;
    private String usuario;
    private String token;

    public ControladorConsola(ClienteEurekaRest cliente) { this.cliente = cliente; }

    public boolean iniciarSesion(String usuario, String contrasena) throws IOException, InterruptedException {
        String token = cliente.iniciarSesion(usuario, contrasena);
        if (token == null || token.isBlank()) return false;
        this.usuario = usuario;
        this.token = token;
        return true;
    }

    public boolean estaAutenticado() { return usuario != null && token != null; }
    public String usuarioActual() { return usuario; }
    public void cerrarSesion() throws IOException, InterruptedException {
        String actual = token;
        token = null;
        usuario = null;
        if (actual != null) cliente.cerrarSesion(actual);
    }

    public List<Movimiento> consultarMovimientos(String cuenta) throws IOException, InterruptedException {
        exigirSesion();
        return cliente.traerMovimientos(cuenta);
    }

    public boolean depositar(String cuenta, BigDecimal importe) throws IOException, InterruptedException {
        exigirSesion();
        return cliente.registrarDeposito(cuenta, importe);
    }

    public boolean transferir(String origen, String destino, BigDecimal importe, String claveOperacion)
            throws IOException, InterruptedException {
        exigirSesion();
        return cliente.transferir(token, origen, destino, importe, claveOperacion);
    }

    private void exigirSesion() {
        if (!estaAutenticado()) throw new IllegalStateException("Inicie sesión para continuar.");
    }
}
