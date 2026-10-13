package ec.edu.monster.clienteescritorio.vista;

import ec.edu.monster.clienteescritorio.modelo.ConversionModelo;
import ec.edu.monster.clienteescritorio.modelo.TipoConversion;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class ConversionVista extends JFrame {

    private final JComboBox<TipoConversion> selectorTipo = new JComboBox<>(TipoConversion.values());
    private final JComboBox<String> selectorOrigen = new JComboBox<>();
    private final JComboBox<String> selectorDestino = new JComboBox<>();
    private final JTextField campoValor = new JTextField();
    private final JTextField campoResultado = new JTextField();
    private final JLabel estado = TemaEscritorio.crearEtiqueta("Listo", 13, false);
    private final JButton botonConvertir = TemaEscritorio.crearBoton("Convertir", true);
    private Runnable accionCerrar;

    public ConversionVista(String usuario) {
        super("Conversor de Unidades GR 9 — " + usuario);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 490));
        setSize(820, 520);
        setLocationRelativeTo(null);

        JPanel principal = new JPanel(new BorderLayout(0, 18));
        principal.setBackground(TemaEscritorio.FONDO);
        principal.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        principal.add(crearEncabezado(usuario), BorderLayout.NORTH);
        principal.add(crearPanelConversion(), BorderLayout.CENTER);
        setContentPane(principal);

        selectorTipo.addActionListener(evento -> actualizarUnidades());
        actualizarUnidades();
        getRootPane().setDefaultButton(botonConvertir);
    }

    private JPanel crearEncabezado(String usuario) {
        JPanel encabezado = new JPanel(new BorderLayout(18, 0));
        encabezado.setBackground(TemaEscritorio.FONDO);

        JPanel titulos = new JPanel();
        titulos.setLayout(new javax.swing.BoxLayout(titulos, javax.swing.BoxLayout.Y_AXIS));
        titulos.setBackground(TemaEscritorio.FONDO);
        titulos.add(TemaEscritorio.crearEtiqueta("Conversor de unidades", 22, true));
        titulos.add(TemaEscritorio.crearEtiqueta("Convierte entre distintas unidades.", 13, false));

        JPanel sesion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sesion.setBackground(TemaEscritorio.FONDO);
        sesion.add(TemaEscritorio.crearEtiqueta("Usuario: " + usuario, 13, false));
        JButton botonCerrar = TemaEscritorio.crearBoton("Cerrar sesión", false);
        botonCerrar.addActionListener(evento -> cerrarSesion());
        sesion.add(botonCerrar);

        encabezado.add(titulos, BorderLayout.CENTER);
        encabezado.add(sesion, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearPanelConversion() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(TemaEscritorio.PANEL);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 22, 18, 22));
        panel.add(TemaEscritorio.crearEtiqueta("Nueva conversión", 16, true), BorderLayout.NORTH);

        TemaEscritorio.estilizarSelector(selectorTipo);
        TemaEscritorio.estilizarSelector(selectorOrigen);
        TemaEscritorio.estilizarSelector(selectorDestino);
        TemaEscritorio.estilizarCampo(campoValor);
        TemaEscritorio.estilizarCampo(campoResultado);
        campoResultado.setEditable(false);
        campoResultado.setFocusable(false);

        Dimension control = new Dimension(150, 32);
        selectorTipo.setPreferredSize(new Dimension(300, control.height));
        selectorOrigen.setPreferredSize(control);
        selectorDestino.setPreferredSize(control);
        campoValor.setPreferredSize(new Dimension(110, control.height));
        campoResultado.setPreferredSize(new Dimension(210, control.height));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(TemaEscritorio.PANEL);
        agregar(formulario, TemaEscritorio.crearEtiqueta("Tipo de conversión", 13, true), 0, 0, 4, 1.0);
        agregar(formulario, selectorTipo, 0, 1, 4, 1.0);
        agregar(formulario, TemaEscritorio.crearEtiqueta("Valor", 13, true), 0, 2, 1, 0.9);
        agregar(formulario, TemaEscritorio.crearEtiqueta("Unidad de origen", 13, true), 1, 2, 1, 1.2);
        agregar(formulario, TemaEscritorio.crearEtiqueta("Unidad de destino", 13, true), 3, 2, 1, 1.2);
        agregar(formulario, campoValor, 0, 3, 1, 0.9);
        agregar(formulario, selectorOrigen, 1, 3, 1, 1.2);
        JLabel flecha = TemaEscritorio.crearEtiqueta("→", 16, false);
        flecha.setHorizontalAlignment(JLabel.CENTER);
        agregar(formulario, flecha, 2, 3, 1, 0.15);
        agregar(formulario, selectorDestino, 3, 3, 1, 1.2);
        panel.add(formulario, BorderLayout.CENTER);
        panel.add(crearPie(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout(18, 0));
        pie.setBackground(TemaEscritorio.PANEL);

        JPanel salida = new JPanel(new BorderLayout(10, 6));
        salida.setBackground(TemaEscritorio.PANEL);
        salida.add(TemaEscritorio.crearEtiqueta("Resultado", 13, true), BorderLayout.WEST);
        salida.add(campoResultado, BorderLayout.CENTER);
        estado.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        salida.add(estado, BorderLayout.SOUTH);

        botonConvertir.setPreferredSize(new Dimension(128, 34));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setBackground(TemaEscritorio.PANEL);
        acciones.add(botonConvertir);

        pie.add(salida, BorderLayout.CENTER);
        pie.add(acciones, BorderLayout.EAST);
        return pie;
    }

    private static void agregar(JPanel panel, Component componente, int columna, int fila,
                                int ancho, double peso) {
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.gridx = columna;
        restricciones.gridy = fila;
        restricciones.gridwidth = ancho;
        restricciones.weightx = peso;
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.anchor = GridBagConstraints.LINE_START;
        restricciones.insets = new Insets(5, 6, 5, 6);
        panel.add(componente, restricciones);
    }

    private void actualizarUnidades() {
        TipoConversion tipo = (TipoConversion) selectorTipo.getSelectedItem();
        if (tipo == null) {
            return;
        }
        selectorOrigen.removeAllItems();
        selectorDestino.removeAllItems();
        for (String unidad : tipo.obtenerUnidades()) {
            selectorOrigen.addItem(unidad);
            selectorDestino.addItem(unidad);
        }
        campoResultado.setText("");
    }

    private void cerrarSesion() {
        if (accionCerrar != null) {
            accionCerrar.run();
        }
    }

    public ConversionModelo obtenerConversion() {
        String valorTexto = campoValor.getText().trim().replace(',', '.');
        if (valorTexto.isEmpty()) {
            throw new IllegalArgumentException("Ingrese el valor que desea convertir.");
        }
        double valor;
        try {
            valor = Double.parseDouble(valorTexto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Ingrese un número válido.", ex);
        }
        if (!Double.isFinite(valor)) {
            throw new IllegalArgumentException("El valor debe ser un número finito.");
        }
        TipoConversion tipo = (TipoConversion) selectorTipo.getSelectedItem();
        return new ConversionModelo(tipo, valor,
                (String) selectorOrigen.getSelectedItem(), (String) selectorDestino.getSelectedItem());
    }

    public void alConvertir(ActionListener accion) {
        botonConvertir.addActionListener(accion);
    }

    public void alCerrarSesion(Runnable accion) {
        accionCerrar = accion;
    }

    public void establecerCargando(boolean cargando) {
        botonConvertir.setEnabled(!cargando);
        if (cargando) {
            estado.setText("Convirtiendo…");
        }
    }

    public void mostrarResultado(double resultado) {
        campoResultado.setText(String.format(Locale.US, "%.10g", resultado));
        estado.setText("Conversión completada.");
    }

    public void mostrarError(String mensaje) {
        estado.setText(mensaje);
    }
}
