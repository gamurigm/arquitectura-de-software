package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public final class LoginVista extends JFrame {
    private final JTextField usuario = new JTextField(22);
    private final JPasswordField contrasena = new JPasswordField(22);
    private final JButton entrar = new JButton("Iniciar sesión");
    private final JLabel estado = new JLabel("Ingrese sus credenciales de EurekaBank.");

    public LoginVista() {
        super("EurekaBank GR09 · Iniciar sesión");
        JPanel contenido = new JPanel(new BorderLayout(16, 20));
        contenido.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        contenido.setBackground(new Color(215, 191, 220));
        JLabel titulo = new JLabel("EUREKABANK", JLabel.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));
        contenido.add(titulo, BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridLayout(0, 1, 6, 8));
        campos.setOpaque(false);
        JLabel etiquetaUsuario = new JLabel("Usuario");
        etiquetaUsuario.setLabelFor(usuario);
        JLabel etiquetaClave = new JLabel("Contraseña");
        etiquetaClave.setLabelFor(contrasena);
        usuario.setName("usuario");
        contrasena.setName("contrasena");
        entrar.setName("iniciarSesion");
        campos.add(etiquetaUsuario);
        campos.add(usuario);
        campos.add(etiquetaClave);
        campos.add(contrasena);
        campos.add(entrar);
        contenido.add(campos, BorderLayout.CENTER);
        contenido.add(estado, BorderLayout.SOUTH);
        setContentPane(contenido);
        getRootPane().setDefaultButton(entrar);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    public String usuario() { return usuario.getText(); }
    public char[] contrasena() { return contrasena.getPassword(); }
    public void limpiarContrasena() { contrasena.setText(""); }
    public void alEntrar(Runnable accion) { entrar.addActionListener(evento -> accion.run()); }
    public void mostrarMensaje(String mensaje) { estado.setText(mensaje); pack(); }

    public void ocupada(boolean ocupada) {
        entrar.setEnabled(!ocupada);
        usuario.setEnabled(!ocupada);
        contrasena.setEnabled(!ocupada);
        setDefaultCloseOperation(ocupada ? DO_NOTHING_ON_CLOSE : EXIT_ON_CLOSE);
    }
}
