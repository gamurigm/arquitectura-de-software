package ec.edu.monster.controlador;

import ec.edu.monster.service.EurekaService;

public class Login {

    public boolean iniciarSesion(String usuario, String contrasena) {
        // Llamar al servicio Eureka para hacer el login
        int resultadoLogin = EurekaService.login(usuario, contrasena);

        // Si el resultado es 1, el login fue exitoso
        return resultadoLogin == 1;
    }
}
