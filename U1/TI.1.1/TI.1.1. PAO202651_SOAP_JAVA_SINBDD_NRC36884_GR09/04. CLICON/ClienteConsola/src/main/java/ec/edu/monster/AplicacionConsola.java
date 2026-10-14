package ec.edu.monster;

import ec.edu.monster.vista.VistaInicioSesion;

public class AplicacionConsola {

    private AplicacionConsola() {
    }

    public static void main(String[] args) {
        new VistaInicioSesion().iniciar();
    }
}

