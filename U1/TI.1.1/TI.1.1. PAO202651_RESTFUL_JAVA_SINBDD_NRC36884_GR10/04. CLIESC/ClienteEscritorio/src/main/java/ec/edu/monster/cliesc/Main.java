package ec.edu.monster.cliesc;

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
