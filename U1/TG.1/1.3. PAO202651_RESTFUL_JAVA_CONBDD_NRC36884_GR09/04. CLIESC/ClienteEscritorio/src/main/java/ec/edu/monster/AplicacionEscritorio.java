package ec.edu.monster;

import ec.edu.monster.controlador.ControladorEscritorio;
import ec.edu.monster.servicios.ClienteEurekaRest;
import ec.edu.monster.vista.VistaEureka;
import javax.swing.SwingUtilities;

public final class AplicacionEscritorio {
    private AplicacionEscritorio() { }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VistaEureka(new ControladorEscritorio(new ClienteEurekaRest())).mostrar());
    }
}
