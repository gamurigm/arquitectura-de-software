package ec.edu.monster.vista;

import ec.edu.monster.controlador.ControladorCliente;
import ec.edu.monster.servicios.ExcepcionApi;

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
 * Ventana principal de conversiones. Usa /api/conversiones/* con el token recibido al iniciar sesión.
 */
public class VentanaConversion extends JFrame {

    private static final Map<String, List<String>> RESPALDO = new LinkedHashMap<>();

    static {
        RESPALDO.put("longitud", List.of("milimetros", "centimetros", "metros", "kilometros", "pies", "yardas", "millas"));
        RESPALDO.put("masa", List.of("miligramos", "gramos", "kilogramos", "toneladas", "onzas", "libras", "quintales"));
        RESPALDO.put("temperatura", List.of("celsius", "fahrenheit", "kelvin", "rankine", "newton", "reaumur"));
    }

    private final ControladorCliente api;
    private final String token;

    private final JComboBox<String> magnitudCombo =
            TemaEscritorio.estilo(new JComboBox<>(new String[]{"Longitud", "Masa", "Temperatura"}));
    private final JComboBox<String> origenCombo = TemaEscritorio.estilo(new JComboBox<>());
    private final JComboBox<String> destinoCombo = TemaEscritorio.estilo(new JComboBox<>());
    private final JTextField valorField = TemaEscritorio.estilo(new JTextField());
    private final JTextField resultadoField = TemaEscritorio.estilo(new JTextField());
    private final JLabel estadoLabel = new JLabel(" ");
    private final JButton convertirButton = new JButton("CONVERTIR");
    private final JButton salirButton = new JButton("Cerrar sesion");

    private Map<String, List<String>> catalogo = new LinkedHashMap<>(RESPALDO);

    public VentanaConversion(ControladorCliente api, String token, String usuario, String url) {
        super("Conversor Monster - " + usuario);
        this.api = api;
        this.token = token;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 560);
        setLocationRelativeTo(null);
        construirInterfaz();
        actualizarUnidades();
        cargarCatalogo();
    }

    private void construirInterfaz() {
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setBackground(TemaEscritorio.FONDO);
        contenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaEscritorio.BORDE),
                BorderFactory.createEmptyBorder(22, 28, 22, 28)));
        setContentPane(contenedor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 16, 10, 16);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 14, 12, 14);

        JLabel titulo = new JLabel("Elija el tipo de unidad para la conversion");
        titulo.setForeground(TemaEscritorio.TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        contenedor.add(titulo, gbc);

        gbc.gridy++;
        JLabel marca = new JLabel("MONSTER  ·  REST JAVA  ·  GR09");
        marca.setForeground(TemaEscritorio.ACENTO);
        marca.setFont(new Font("SansSerif", Font.BOLD, 12));
        contenedor.add(marca, gbc);

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
        resultadoField.setForeground(TemaEscritorio.ACENTO);
        contenedor.add(resultadoField, gbc);

        styleButton(convertirButton);
        convertirButton.addActionListener(e -> convertir());
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contenedor.add(convertirButton, gbc);

        estadoLabel.setForeground(TemaEscritorio.ERROR);
        gbc.gridy++;
        contenedor.add(estadoLabel, gbc);

        salirButton.setForeground(TemaEscritorio.TEXTO_SECUNDARIO);
        salirButton.setBackground(TemaEscritorio.FONDO);
        salirButton.setBorderPainted(false);
        salirButton.setFocusPainted(false);
        salirButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        salirButton.addActionListener(e -> cerrarSesion());
        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(TemaEscritorio.FONDO);
        sur.add(salirButton, BorderLayout.EAST);
        add(sur, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(convertirButton);
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
                return api.obtenerCatalogo();
            }

            @Override
            protected void done() {
                try {
                    JsonNode magnitudes = get().path("magnitudes");
                    Map<String, List<String>> nuevo = new LinkedHashMap<>();
                    magnitudes.properties().forEach(entrada -> {
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
            if (!Double.isFinite(valor)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            estadoLabel.setText("Ingrese un número finito válido.");
            return;
        }
        String magnitud = magnitudSeleccionada();
        if ("masa".equals(magnitud) && valor < 0) {
            estadoLabel.setText("La masa no puede ser negativa.");
            return;
        }
        if (origenCombo.getSelectedItem() == null || destinoCombo.getSelectedItem() == null) {
            estadoLabel.setText("Seleccione las unidades de origen y destino.");
            return;
        }
        String origen = String.valueOf(origenCombo.getSelectedItem());
        String destino = String.valueOf(destinoCombo.getSelectedItem());

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
                    double resultado = respuesta.path("resultado").asDouble(Double.NaN);
                    if (!Double.isFinite(resultado)) {
                        throw new IllegalArgumentException("El servidor devolvió un resultado inválido.");
                    }
                    resultadoField.setText(String.valueOf(resultado));
                    estadoLabel.setForeground(TemaEscritorio.ACENTO);
                    estadoLabel.setText(respuesta.path("mensaje").asText());
                } catch (Exception ex) {
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;
                    estadoLabel.setForeground(TemaEscritorio.ERROR);
                    estadoLabel.setText(causa.getMessage());
                    if (causa instanceof ExcepcionApi apiEx && apiEx.esNoAutorizado()) {
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
        VentanaInicioSesion login = new VentanaInicioSesion();
        if (mensaje != null) {
            // se muestra el aviso una vez abierta la ventana
            javax.swing.SwingUtilities.invokeLater(() -> javax.swing.JOptionPane.showMessageDialog(
                    login, mensaje, "Sesion", javax.swing.JOptionPane.WARNING_MESSAGE));
        }
        login.setVisible(true);
    }
}
