package wseurekaclienteconsola;

import ec.edu.monster.controlador.EurekaController;
import ec.edu.monster.controlador.Login;
import ec.edu.monster.service.EurekaService;
import ec.edu.monster.vista.MenuPrincipal;

public final class WSEurekaClienteConsola {
    private WSEurekaClienteConsola() {
    }

    public static void main(String[] args) {
        EurekaService servicio = new EurekaService();
        MenuPrincipal vista = new MenuPrincipal(
                new Login(servicio), new EurekaController(servicio));
        vista.iniciar();
    }
}
