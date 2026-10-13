package ec.edu.monster.clicon;

import ec.edu.monster.clicon.controlador.ControladorConsola;

public class Main {
    public static void main(String[] args) {
        if (System.console() == null) {
            System.err.println("Abra una terminal interactiva y ejecute java -jar target/ClienteConsola.jar.");
            return;
        }
        new ControladorConsola().iniciar();
    }
}
