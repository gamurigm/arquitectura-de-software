package ec.edu.monster.clienteescritorio.modelo;

import java.time.Duration;
import java.time.Instant;

public final class SesionModelo {

    private String usuario;
    private String token;
    private Instant vence;

    public void iniciar(String usuario, String token) {
        this.usuario = usuario;
        this.token = token;
        this.vence = Instant.now().plus(Duration.ofMinutes(15));
    }

    public boolean estaVigente() {
        return token != null && vence != null && vence.isAfter(Instant.now());
    }

    public String obtenerUsuario() {
        return usuario;
    }

    public String obtenerToken() {
        return token;
    }

    public void cerrar() {
        usuario = null;
        token = null;
        vence = null;
    }
}
