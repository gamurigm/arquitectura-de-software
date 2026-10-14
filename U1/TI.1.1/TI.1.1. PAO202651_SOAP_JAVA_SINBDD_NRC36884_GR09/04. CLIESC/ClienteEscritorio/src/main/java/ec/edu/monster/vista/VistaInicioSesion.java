package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.net.URL;
import java.util.Objects;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.event.ActionListener;

public final class VistaInicioSesion extends JFrame {

    private final JTextField campoUsuario = new JTextField("Monster");
    private final JPasswordField campoContrasenia = new JPasswordField("Monster9");
    private final JButton botonIngresar = TemaEscritorio.crearBoton("Iniciar sesión", true);

    public VistaInicioSesion() {
        super("Conversor de Unidades GR 9");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(820, 480);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new GridBagLayout());
        principal.setBackground(TemaEscritorio.FONDO);

        JPanel tarjeta = new JPanel(new BorderLayout(24, 0));
        tarjeta.setBackground(TemaEscritorio.PANEL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaEscritorio.BORDE),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        JPanel formulario = crearFormulario();
        JPanel formularioCentrado = new JPanel(new GridBagLayout());
        formularioCentrado.setBackground(TemaEscritorio.PANEL);
        formularioCentrado.add(formulario);
        tarjeta.add(formularioCentrado, BorderLayout.CENTER);
        tarjeta.add(crearImagen(), BorderLayout.EAST);
        principal.add(tarjeta);
        setContentPane(principal);
        getRootPane().setDefaultButton(botonIngresar);
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBackground(TemaEscritorio.PANEL);

        JLabel titulo = TemaEscritorio.crearEtiqueta("Iniciar sesión", 28, true);
        JLabel subtitulo = TemaEscritorio.crearEtiqueta("Ingresa tus credenciales para continuar.", 13, false);
        JLabel etiquetaUsuario = TemaEscritorio.crearEtiqueta("Usuario", 13, true);
        JLabel etiquetaContrasenia = TemaEscritorio.crearEtiqueta("Contraseña", 13, true);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        etiquetaUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        etiquetaContrasenia.setAlignmentX(Component.LEFT_ALIGNMENT);
        TemaEscritorio.estilizarCampo(campoUsuario);
        TemaEscritorio.estilizarCampo(campoContrasenia);

        Dimension campo = new Dimension(340, 46);
        campoUsuario.setPreferredSize(campo);
        campoUsuario.setMaximumSize(campo);
        campoUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoContrasenia.setPreferredSize(campo);
        campoContrasenia.setMaximumSize(campo);
        campoContrasenia.setAlignmentX(Component.LEFT_ALIGNMENT);
        botonIngresar.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension boton = new Dimension(campo.width, 46);
        botonIngresar.setPreferredSize(boton);
        botonIngresar.setMaximumSize(boton);

        formulario.add(titulo);
        formulario.add(Box.createVerticalStrut(4));
        formulario.add(subtitulo);
        formulario.add(Box.createVerticalStrut(20));
        formulario.add(etiquetaUsuario);
        formulario.add(Box.createVerticalStrut(6));
        formulario.add(campoUsuario);
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(etiquetaContrasenia);
        formulario.add(Box.createVerticalStrut(6));
        formulario.add(campoContrasenia);
        formulario.add(Box.createVerticalStrut(18));
        formulario.add(botonIngresar);
        return formulario;
    }

    private JLabel crearImagen() {
        URL recurso = Objects.requireNonNull(
                VistaInicioSesion.class.getResource("/Imagenes/imagen_inicio_sesion.jpg"),
                "No se encontró la imagen imagen_inicio_sesion.jpg.");
        Image imagenOriginal = new ImageIcon(recurso).getImage();
        Image imagenAjustada = imagenOriginal.getScaledInstance(172, 300, Image.SCALE_SMOOTH);
        JLabel imagen = new JLabel(new ImageIcon(imagenAjustada));
        imagen.setToolTipText("Monster");
        imagen.setBorder(BorderFactory.createLineBorder(TemaEscritorio.BORDE));
        imagen.setPreferredSize(new Dimension(172, 300));
        return imagen;
    }

    public String obtenerUsuario() {
        return campoUsuario.getText().trim();
    }

    public String obtenerContrasenia() {
        return new String(campoContrasenia.getPassword());
    }

    public void alIngresar(ActionListener accion) {
        botonIngresar.addActionListener(accion);
    }

    public void establecerCargando(boolean cargando) {
        botonIngresar.setEnabled(!cargando);
        botonIngresar.setText(cargando ? "Conectando…" : "Iniciar sesión");
    }
}
