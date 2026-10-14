package ec.edu.monster.vista;

import ec.edu.monster.controlador.ControladorCliente;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Ventana de inicio de sesión. Valida las credenciales con /api/auth/login.
 */
public class VentanaInicioSesion extends JFrame {

    private final JTextField urlField = TemaEscritorio.estilo(new JTextField("http://localhost:8080", 18));
    private final JTextField usuarioField = TemaEscritorio.estilo(new JTextField("Monster", 14));
    private final JPasswordField claveField = TemaEscritorio.estilo(new JPasswordField("Monster9", 14));
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton loginButton = new JButton("INICIAR SESION");

    public VentanaInicioSesion() {
        super("Login - Conversor Monster");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 520);
        setLocationRelativeTo(null);
        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(TemaEscritorio.FONDO);
        contenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaEscritorio.BORDE),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)));
        setContentPane(contenedor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 14, 9, 14);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Conversor de unidades");
        titulo.setForeground(TemaEscritorio.TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 25));
        contenedor.add(titulo, gbc);

        gbc.gridy++;
        JLabel marca = new JLabel("MONSTER  ·  REST JAVA  ·  GR09");
        marca.setForeground(TemaEscritorio.ACENTO);
        marca.setFont(new Font("SansSerif", Font.BOLD, 12));
        contenedor.add(marca, gbc);
        gbc.gridy++;
        contenedor.add(etiqueta("Servidor:"), gbc);
        gbc.gridy++;
        contenedor.add(urlField, gbc);

        gbc.gridy++;
        contenedor.add(etiqueta("Usuario:"), gbc);
        gbc.gridy++;
        contenedor.add(usuarioField, gbc);

        gbc.gridy++;
        contenedor.add(etiqueta("Clave:"), gbc);
        gbc.gridy++;
        contenedor.add(claveField, gbc);

        styleButton(loginButton);
        loginButton.addActionListener(e -> iniciarSesion());
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contenedor.add(loginButton, gbc);

        errorLabel.setForeground(TemaEscritorio.ERROR);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contenedor.add(errorLabel, gbc);

        getRootPane().setDefaultButton(loginButton);
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(TemaEscritorio.TEXTO);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }

    private void styleButton(JButton boton) {
        boton.setBackground(TemaEscritorio.ACENTO);
        boton.setForeground(TemaEscritorio.FONDO);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(240, 44));
    }

    private void iniciarSesion() {
        String url = urlField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String clave = new String(claveField.getPassword());

        errorLabel.setText(" ");
        if (!urlValida(url)) {
            errorLabel.setText("Ingrese una URL HTTP o HTTPS válida.");
            return;
        }
        if (usuario.isBlank() || clave.isBlank()) {
            errorLabel.setText("Ingrese usuario y contraseña.");
            return;
        }
        loginButton.setEnabled(false);
        loginButton.setText("Conectando...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                ControladorCliente api = new ControladorCliente(url);
                return api.iniciarSesion(usuario, clave);
            }

            @Override
            protected void done() {
                try {
                    String token = get();
                    ControladorCliente api = new ControladorCliente(url);
                    new VentanaConversion(api, token, usuario, url).setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                    errorLabel.setText(causa.getMessage());
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("INICIAR SESION");
                }
            }
        }.execute();
    }

    private static boolean urlValida(String valor) {
        try {
            java.net.URI uri = java.net.URI.create(valor);
            return uri.isAbsolute() && uri.getHost() != null
                    && ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getUserInfo() == null && uri.getQuery() == null && uri.getFragment() == null;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
