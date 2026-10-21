package wseurekaclienteescritorio;

import ec.edu.monster.controlador.EurekaControlador;
import ec.edu.monster.service.EurekaService;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class WSEurekaClienteEscritorio {
    private WSEurekaClienteEscritorio() { }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            EurekaControlador controlador = new EurekaControlador(new EurekaService());
            SwingUtilities.invokeLater(controlador::iniciar);
        } catch (Exception error) {
            System.err.println("No se pudo iniciar EurekaBank: " + error.getMessage());
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "No se pudo iniciar EurekaBank. Revise la dirección del servicio.",
                    "EurekaBank", JOptionPane.ERROR_MESSAGE));
        }
    }
}
