package ec.edu.monster.cliesc;

import com.fasterxml.jackson.databind.JsonNode;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ventana principal de conversiones. Consume /api/conversiones/* con el token obtenido en el login.
 */
public class ConversionFrame extends JFrame {

    private static final Map<String, List<String>> RESPALDO = new LinkedHashMap<>();

    static {
        RESPALDO.put("longitud", List.of("centimetros", "metros", "pies", "yardas", "millas"));
        RESPALDO.put("masa", List.of("gramos", "kilogramos", "onzas", "libras", "quintales"));
        RESPALDO.put("temperatura", List.of("fahrenheit", "kelvin", "celsius", "newton", "reaumur"));
    }

    private final ApiClient api;
    private final String token;
    private final String usuario;
    private final String url;

    private final JComboBox<String> magnitudCombo =
            Theme.estilo(new JComboBox<>(new String[]{"Longitud", "Masa", "Temperatura"}));
    private final JComboBox<String> origenCombo = Theme.estilo(new JComboBox<>());
    private final JComboBox<String> destinoCombo = Theme.estilo(new JComboBox<>());
    private final JTextField valorField = Theme.estilo(new JTextField());
    private final JTextField resultadoField = Theme.estilo(new JTextField());
    private final JLabel estadoLabel = new JLabel(" ");
    private final JButton convertirButton = new JButton("CONVERTIR");
    private final JButton salirButton = new JButton("Cerrar sesion");

    private Map<String, List<String>> catalogo = new LinkedHashMap<>(RESPALDO);

    public ConversionFrame(ApiClient api, String token, String usuario, String url) {
        super("Conversor Monster - " + usuario);
        this.api = api;
        this.token = token;
        this.usuario = usuario;
        this.url = url;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(560, 460);
        setLocationRelativeTo(null);
        construirInterfaz();
        actualizarUnidades();
        cargarCatalogo();
    }

    private void construirInterfaz() {
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(Theme.FONDO);
        setContentPane(contenedor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 14, 8, 14);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titulo = new JLabel("Elija el tipo de unidad para la conversion");
        titulo.setForeground(Theme.TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        contenedor.add(titulo, gbc);

        gbc.gridy++;
        contenedor.add(etiqueta("Magnitud:"), gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contenedor.add(magnitudCombo, gbc);
        magnitudCombo.addActionListener(e -> actualizarUnidades());

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        contenedor.add(etiqueta("Unidad de origen"), gbc);
        gbc.gridx = 1;
        contenedor.add(etiqueta("Unidad de destino"), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contenedor.add(origenCombo, gbc);
        gbc.gridx = 1;
        contenedor.add(destinoCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        contenedor.add(etiqueta("Valor"), gbc);
        gbc.gridx = 1;
        contenedor.add(etiqueta("Resultado"), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contenedor.add(valorField, gbc);
        gbc.gridx = 1;
        resultadoField.setEditable(false);
        contenedor.add(resultadoField, gbc);

        styleButton(convertirButton);
        convertirButton.addActionListener(e -> convertir());
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contenedor.add(convertirButton, gbc);

        estadoLabel.setForeground(Theme.ERROR);
        gbc.gridy++;
        contenedor.add(estadoLabel, gbc);

        salirButton.setForeground(new Color(0x88, 0x88, 0x88));
        salirButton.setBackground(Theme.FONDO);
        salirButton.setBorderPainted(false);
        salirButton.setFocusPainted(false);
        salirButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        salirButton.addActionListener(e -> cerrarSesion());
        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(Theme.FONDO);
        sur.add(salirButton, BorderLayout.EAST);
        add(sur, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(convertirButton);
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
        boton.setFont(new Font("SansSerif", Font.BOLD, 15));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(240, 46));
    }

    private String magnitudSeleccionada() {
        return String.valueOf(magnitudCombo.getSelectedItem()).toLowerCase();
    }

    private void actualizarUnidades() {
        List<String> unidades = catalogo.getOrDefault(magnitudSeleccionada(),
                RESPALDO.getOrDefault(magnitudSeleccionada(), List.of()));
        DefaultComboBoxModel<String> modeloOrigen = new DefaultComboBoxModel<>(unidades.toArray(String[]::new));
        DefaultComboBoxModel<String> modeloDestino = new DefaultComboBoxModel<>(unidades.toArray(String[]::new));
        origenCombo.setModel(modeloOrigen);
        destinoCombo.setModel(modeloDestino);
        valorField.setText("");
        resultadoField.setText("");
        estadoLabel.setText(" ");
    }

    private void cargarCatalogo() {
        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return api.catalogo();
            }

            @Override
            protected void done() {
                try {
                    JsonNode magnitudes = get().path("magnitudes");
                    Map<String, List<String>> nuevo = new LinkedHashMap<>();
                    magnitudes.fields().forEachRemaining(entrada -> {
                        List<String> unidades = new java.util.ArrayList<>();
                        entrada.getValue().forEach(n -> unidades.add(n.asText()));
                        nuevo.put(entrada.getKey(), unidades);
                    });
                    if (!nuevo.isEmpty()) {
                        catalogo = nuevo;
                        actualizarUnidades();
                    }
                } catch (Exception ignored) {
                    // se mantiene el catalogo de respaldo
                }
            }
        }.execute();
    }

    private void convertir() {
        double valor;
        try {
            valor = Double.parseDouble(valorField.getText().trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            estadoLabel.setText("Ingrese un valor numerico valido.");
            return;
        }
        String origen = String.valueOf(origenCombo.getSelectedItem());
        String destino = String.valueOf(destinoCombo.getSelectedItem());
        String magnitud = magnitudSeleccionada();

        estadoLabel.setText(" ");
        convertirButton.setEnabled(false);
        convertirButton.setText("Convirtiendo...");

        new SwingWorker<JsonNode, Void>() {
            @Override
            protected JsonNode doInBackground() throws Exception {
                return api.convertir(magnitud, valor, origen, destino, token);
            }

            @Override
            protected void done() {
                try {
                    JsonNode respuesta = get();
                    resultadoField.setText(String.valueOf(respuesta.path("resultado").asDouble()));
                    estadoLabel.setForeground(new Color(0x7E, 0xD3, 0x7E));
                    estadoLabel.setText(respuesta.path("mensaje").asText());
                } catch (Exception ex) {
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                    estadoLabel.setForeground(Theme.ERROR);
                    estadoLabel.setText(causa.getMessage());
                    if (causa instanceof ApiException apiEx && apiEx.esNoAutorizado()) {
                        volverAlLogin("La sesion expiro. Inicie sesion nuevamente.");
                    }
                } finally {
                    convertirButton.setEnabled(true);
                    convertirButton.setText("CONVERTIR");
                }
            }
        }.execute();
    }

    private void cerrarSesion() {
        volverAlLogin(null);
    }

    private void volverAlLogin(String mensaje) {
        dispose();
        LoginFrame login = new LoginFrame();
        if (mensaje != null) {
            // se muestra el aviso una vez abierta la ventana
            javax.swing.SwingUtilities.invokeLater(() -> javax.swing.JOptionPane.showMessageDialog(
                    login, mensaje, "Sesion", javax.swing.JOptionPane.WARNING_MESSAGE));
        }
        login.setVisible(true);
    }
}
