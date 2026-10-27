package ec.edu.monster.vista;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

final class TemaEureka {
    static final Color TINTA = new Color(23, 50, 77);
    static final Color AZUL = new Color(8, 127, 123);
    static final Color SUAVE = new Color(102, 123, 142);

    private TemaEureka() {}

    static void ventana(JFrame ventana, Tarjeta tarjeta, int ancho) { fondo(ventana, tarjeta, ancho); }
    static void ventana(JDialog ventana, Tarjeta tarjeta, int ancho) { fondo(ventana, tarjeta, ancho); }

    private static void fondo(java.awt.Window ventana, Tarjeta tarjeta, int ancho) {
        tarjeta.setPreferredSize(new java.awt.Dimension(ancho, tarjeta.getPreferredSize().height));
        Fondo fondo = new Fondo(new GridBagLayout());
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.insets = new Insets(28, 28, 28, 28);
        fondo.add(tarjeta, restricciones);
        if (ventana instanceof JFrame frame) frame.setContentPane(fondo);
        else if (ventana instanceof JDialog dialogo) dialogo.setContentPane(fondo);
    }

    static void titulo(JComponent componente, float tamano) {
        componente.setForeground(TINTA);
        componente.setFont(componente.getFont().deriveFont(java.awt.Font.BOLD, tamano));
    }

    static void etiqueta(JComponent componente) {
        componente.setForeground(SUAVE);
        componente.setFont(componente.getFont().deriveFont(java.awt.Font.PLAIN, 13f));
    }

    static void campo(JTextField campo) {
        campo.setFont(campo.getFont().deriveFont(15f));
        campo.setForeground(TINTA);
        campo.setCaretColor(AZUL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 207, 230), 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        campo.setBackground(new Color(255, 255, 255, 225));
    }

    static void campo(JComboBox<?> campo) {
        campo.setFont(campo.getFont().deriveFont(15f));
        campo.setForeground(TINTA);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 207, 230), 1, true),
                new EmptyBorder(8, 8, 8, 8)));
        campo.setBackground(new Color(255, 255, 255, 225));
    }

    static void boton(JButton boton, boolean primario) {
        boton.setFont(boton.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        boton.setForeground(primario ? Color.WHITE : AZUL);
        boton.setBackground(primario ? AZUL : new Color(232, 240, 252));
        boton.setBorder(new EmptyBorder(11, 17, 11, 17));
        boton.setFocusPainted(false);
    }

    static final class Tarjeta extends JPanel {
        Tarjeta(java.awt.LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(28, 32, 28, 32));
        }

        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(255, 255, 255, 218));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
            g.setColor(new Color(255, 255, 255, 235));
            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 27, 27);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class Fondo extends JPanel {
        Fondo(java.awt.LayoutManager layout) { super(layout); }

        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, new Color(12, 35, 73),
                    getWidth(), getHeight(), new Color(25, 105, 149)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(103, 209, 230, 38));
            g.fillOval(getWidth() - 240, -150, 360, 360);
            g.setColor(new Color(72, 135, 238, 34));
            g.fillOval(-170, getHeight() - 220, 390, 390);
            g.dispose();
        }
    }
}
