package ec.edu.monster;

import ec.edu.monster.vista.VentanaInicioSesion;
import ec.edu.monster.vista.TemaEscritorio;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del cliente de escritorio (Java Swing).
 */
public class AplicacionEscritorio {

    public static void main(String[] args) {
        TemaEscritorio.aplicarApariencia();
        SwingUtilities.invokeLater(() -> new VentanaInicioSesion().setVisible(true));
    }
}
