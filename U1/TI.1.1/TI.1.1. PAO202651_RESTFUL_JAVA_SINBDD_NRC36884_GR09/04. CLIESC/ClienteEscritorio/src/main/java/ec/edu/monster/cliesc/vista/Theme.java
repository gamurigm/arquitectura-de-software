package ec.edu.monster.cliesc.vista;

import javax.swing.JComponent;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

/**
 * Paleta y utilidades de estilo compartidas por las ventanas (misma linea grafica que los demas clientes).
 */
public final class Theme {

    public static final Color FONDO = new Color(0x12, 0x12, 0x12);
    public static final Color PANEL = new Color(0x1A, 0x1A, 0x1A);
    public static final Color CAMPO = new Color(0x40, 0x40, 0x40);
    public static final Color BORDE = new Color(0xE0, 0xE0, 0xFA);
    public static final Color AZUL = new Color(0x5B, 0x88, 0xE5);
    public static final Color AZUL_HOVER = new Color(0x4A, 0x75, 0xC9);
    public static final Color TEXTO = Color.WHITE;
    public static final Color ERROR = new Color(0xFF, 0x4D, 0x4D);

    private Theme() {
    }

    public static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // se usa el look and feel por defecto
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends JComponent> T estilo(T componente) {
        componente.setBackground(CAMPO);
        componente.setForeground(TEXTO);
        componente.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return componente;
    }
}
