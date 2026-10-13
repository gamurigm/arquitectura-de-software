package ec.edu.monster.cliesc;

import ec.edu.monster.cliesc.vista.LoginFrame;
import ec.edu.monster.cliesc.vista.Theme;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del cliente de escritorio (Java Swing).
 */
public class Main {

    public static void main(String[] args) {
        Theme.aplicarLookAndFeel();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
