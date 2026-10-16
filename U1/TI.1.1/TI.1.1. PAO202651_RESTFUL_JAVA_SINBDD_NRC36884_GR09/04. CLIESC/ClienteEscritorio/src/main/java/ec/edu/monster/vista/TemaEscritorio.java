package ec.edu.monster.vista;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

/**
 * Paleta y utilidades de estilo compartidas por las ventanas (misma línea gráfica que los demas clientes).
 */
public final class TemaEscritorio {

    public static final Color FONDO = Color.decode("#0B1020");
    public static final Color PANEL = Color.decode("#121C30");
    public static final Color CAMPO = Color.decode("#17243A");
    public static final Color BORDE = Color.decode("#2D3A55");
    public static final Color ACENTO = Color.decode("#52E0D2");
    public static final Color VIOLETA = Color.decode("#A78BFA");
    public static final Color TEXTO = Color.decode("#F4F7FC");
    public static final Color TEXTO_SECUNDARIO = Color.decode("#A9B7CB");
    public static final Color ERROR = Color.decode("#FF8585");

    private TemaEscritorio() {
    }

    public static void aplicarApariencia() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // se usa el apariencia por defecto
        }
    }

    public static <T extends JComponent> T estilo(T componente) {
        componente.setBackground(CAMPO);
        componente.setForeground(TEXTO);
        componente.setFont(new Font("SansSerif", Font.PLAIN, 14));
        componente.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return componente;
    }
}
