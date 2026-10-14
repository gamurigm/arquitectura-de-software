package ec.edu.monster;

import ec.edu.monster.controlador.ControladorConsola;

public class AplicacionConsola {
    public static void main(String[] args) {
        if (System.console() == null) {
            System.err.println("Abra una terminal interactiva y ejecute java -jar target/ClienteConsola.jar.");
            return;
        }
        new ControladorConsola().iniciar();
    }
}
