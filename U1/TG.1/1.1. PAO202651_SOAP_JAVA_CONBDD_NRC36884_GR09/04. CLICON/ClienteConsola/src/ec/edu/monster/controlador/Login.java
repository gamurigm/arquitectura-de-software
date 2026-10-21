package ec.edu.monster.controlador;

import ec.edu.monster.service.EurekaService;

public final class Login {
    private final EurekaService servicio;

    public Login(EurekaService servicio) {
        this.servicio = servicio;
    }

    public boolean iniciarSesion(String usuario, String contrasena) {
        return servicio.login(usuario, contrasena) == 1;
    }
}
