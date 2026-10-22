package ec.edu.monster.controlador;

import ec.edu.monster.service.EurekaService;

public final class Login {
    private final EurekaService servicio;

    public Login(EurekaService servicio) {
        this.servicio = servicio;
    }

    public String iniciarSesion(String usuario, String contrasena) throws java.io.IOException {
        return servicio.crearSesion(usuario, contrasena);
    }
}
