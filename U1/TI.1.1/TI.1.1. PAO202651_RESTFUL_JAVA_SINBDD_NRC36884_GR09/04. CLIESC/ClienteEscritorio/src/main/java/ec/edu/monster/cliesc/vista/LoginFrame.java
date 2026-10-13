package ec.edu.monster.cliesc.vista;

import ec.edu.monster.cliesc.controlador.ControladorCliente;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Ventana de inicio de sesion. Valida credenciales contra /api/auth/login.
 */
public class LoginFrame extends JFrame {

    private final JTextField urlField = Theme.estilo(new JTextField("http://localhost:8080", 18));
    private final JTextField usuarioField = Theme.estilo(new JTextField("monster", 14));
    private final JPasswordField claveField = Theme.estilo(new JPasswordField("monster9", 14));
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JButton loginButton = new JButton("INICIAR SESION");

    public LoginFrame() {
        super("Login - Conversor Monster");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 360);
        setLocationRelativeTo(null);
        construirInterfaz();
    }

    private void construirInterfaz() {
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(Theme.FONDO);
        setContentPane(contenedor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Ingrese sus credenciales");
        titulo.setForeground(Theme.TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        contenedor.add(titulo, gbc);

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

        errorLabel.setForeground(Theme.ERROR);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contenedor.add(errorLabel, gbc);

        getRootPane().setDefaultButton(loginButton);
    }

    private JLabel etiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Theme.TEXTO);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        return label;
    }

    private void styleButton(JButton boton) {
        boton.setBackground(Theme.AZUL);
        boton.setForeground(Color.WHITE);
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
        loginButton.setEnabled(false);
        loginButton.setText("Conectando...");

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                ControladorCliente api = new ControladorCliente(url);
                return api.login(usuario, clave);
            }

            @Override
            protected void done() {
                try {
                    String token = get();
                    ControladorCliente api = new ControladorCliente(url);
                    new ConversionFrame(api, token, usuario, url).setVisible(true);
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
}
