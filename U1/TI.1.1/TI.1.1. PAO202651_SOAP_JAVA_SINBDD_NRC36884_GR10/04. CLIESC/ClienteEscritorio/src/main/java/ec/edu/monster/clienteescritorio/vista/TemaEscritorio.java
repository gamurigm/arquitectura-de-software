package ec.edu.monster.clienteescritorio.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

final class TemaEscritorio {

    static final Color FONDO = Color.decode("#121212");
    static final Color PANEL = Color.decode("#151515");
    static final Color CAMPO = Color.decode("#404040");
    static final Color TEXTO = Color.decode("#FFFFFF");
    static final Color BORDE = Color.decode("#333333");
    static final Color AZUL = Color.decode("#5B88E5");

    private TemaEscritorio() {
    }

    static JLabel crearEtiqueta(String texto, int tamano, boolean negrita) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(TEXTO);
        etiqueta.setFont(new Font(Font.SANS_SERIF, negrita ? Font.BOLD : Font.PLAIN, tamano));
        return etiqueta;
    }

    static void estilizarCampo(JTextField campo) {
        campo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setBackground(CAMPO);
        campo.setCaretColor(TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        campo.setOpaque(true);
    }

    static void estilizarSelector(JComboBox<?> selector) {
        selector.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        selector.setForeground(TEXTO);
        selector.setBackground(CAMPO);
        selector.setBorder(BorderFactory.createLineBorder(BORDE));
        selector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                           boolean seleccionado, boolean tieneFoco) {
                JLabel etiqueta = (JLabel) super.getListCellRendererComponent(
                        lista, valor, indice, seleccionado, tieneFoco);
                etiqueta.setBackground(seleccionado ? AZUL : CAMPO);
                etiqueta.setForeground(TEXTO);
                etiqueta.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return etiqueta;
            }
        });
    }

    static JButton crearBoton(String texto, boolean principal) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        boton.setForeground(TEXTO);
        boton.setBackground(principal ? AZUL : PANEL);
        boton.setBorder(principal
                ? BorderFactory.createEmptyBorder(8, 16, 8, 16)
                : BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE),
                        BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }
}
