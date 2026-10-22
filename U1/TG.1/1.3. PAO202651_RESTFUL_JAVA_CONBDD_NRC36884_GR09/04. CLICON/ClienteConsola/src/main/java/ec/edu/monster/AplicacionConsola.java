package ec.edu.monster;

import ec.edu.monster.controlador.ControladorConsola;
import ec.edu.monster.servicios.ClienteEurekaRest;
import ec.edu.monster.vista.ConsolaVista;

public final class AplicacionConsola {
    private AplicacionConsola() { }

    public static void main(String[] args) {
        new ConsolaVista(new ControladorConsola(new ClienteEurekaRest())).iniciar();
    }
}
