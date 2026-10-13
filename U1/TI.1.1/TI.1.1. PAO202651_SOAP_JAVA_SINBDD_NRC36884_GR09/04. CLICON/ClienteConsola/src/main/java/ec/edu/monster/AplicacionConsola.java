package ec.edu.monster;

import ec.edu.monster.vista.LoginVista;

public class AplicacionConsola {

    private AplicacionConsola() {
    }

    public static void main(String[] args) {
        new LoginVista().iniciar();
    }
}

