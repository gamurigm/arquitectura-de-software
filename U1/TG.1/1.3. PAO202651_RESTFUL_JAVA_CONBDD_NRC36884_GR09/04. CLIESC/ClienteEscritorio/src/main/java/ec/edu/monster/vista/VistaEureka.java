package ec.edu.monster.vista;

import ec.edu.monster.controlador.ControladorEscritorio;
import ec.edu.monster.modelo.Movimiento;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public final class VistaEureka {
    private static final Color AZUL = new Color(18, 48, 89);
    private static final Color TURQUESA = new Color(17, 142, 155);
    private final ControladorEscritorio controlador;
    private final JFrame ventana = new JFrame("EurekaBank | Banca personal");
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final JTextField usuario = new JTextField(22);
    private final JPasswordField contrasena = new JPasswordField(22);
    private final JTextField cuenta = new JTextField(12);
    private final JTextField importe = new JTextField(12);
    private final JTextField cuentaOrigen = new JTextField(9);
    private final JTextField cuentaDestino = new JTextField(9);
    private final JTextField importeTransferencia = new JTextField(9);
    private final JLabel estado = new JLabel("Consulta movimientos o registra un depósito.");
    private final DefaultTableModel tablaModelo = new DefaultTableModel(
            new Object[]{"N.º", "Cuenta", "Fecha", "Tipo", "Acción", "Importe"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public VistaEureka(ControladorEscritorio controlador) {
        this.controlador = controlador;
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setMinimumSize(new Dimension(760, 600));
        ventana.setSize(880, 690);
        ventana.setLocationRelativeTo(null);
        contenido.add(crearLogin(), "login");
        contenido.add(crearPanelPrincipal(), "principal");
        ventana.setContentPane(contenido);
        tarjetas.show(contenido, "login");
    }

    public void mostrar() { ventana.setVisible(true); }

    private JPanel crearLogin() {
        JPanel exterior = new Fondo();
        exterior.setLayout(new GridBagLayout());
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(new Color(255, 255, 255, 244));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(215, 227, 238)), BorderFactory.createEmptyBorder(34, 40, 34, 40)));
        GridBagConstraints g = restricciones();
        JLabel marca = new JLabel("EUREKABANK", SwingConstants.CENTER);
        marca.setForeground(AZUL);
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 26f));
        JLabel bajada = new JLabel("Banca personal · acceso seguro", SwingConstants.CENTER);
        bajada.setForeground(new Color(91, 110, 135));
        JLabel lUsuario = new JLabel("Usuario");
        JLabel lClave = new JLabel("Contraseña");
        lUsuario.setLabelFor(usuario);
        lClave.setLabelFor(contrasena);
        usuario.getAccessibleContext().setAccessibleName("Usuario");
        contrasena.getAccessibleContext().setAccessibleName("Contraseña");
        JButton ingresar = boton("Iniciar sesión", true);
        ingresar.addActionListener(e -> ejecutar(() -> controlador.iniciarSesion(usuario.getText().strip(), new String(contrasena.getPassword())), ok -> {
            if (ok) { tarjetas.show(contenido, "principal"); estado.setText("Sesión iniciada para " + controlador.usuarioActual()); }
            else aviso("Usuario o contraseña incorrectos.");
        }));
        agregar(tarjeta, marca, g, 0, 0, 2);
        agregar(tarjeta, bajada, g, 0, 1, 2);
        agregar(tarjeta, lUsuario, g, 0, 2, 2);
        agregar(tarjeta, usuario, g, 0, 3, 2);
        agregar(tarjeta, lClave, g, 0, 4, 2);
        agregar(tarjeta, contrasena, g, 0, 5, 2);
        agregar(tarjeta, ingresar, g, 0, 6, 2);
        exterior.add(tarjeta);
        return exterior;
    }

    private JPanel crearPanelPrincipal() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(new Color(243, 247, 251));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 22, 28));
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        JLabel titulo = new JLabel("EurekaBank");
        titulo.setForeground(AZUL);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 25f));
        JButton salir = boton("Cerrar sesión", false);
        salir.addActionListener(e -> ejecutar(() -> { controlador.cerrarSesion(); return true; }, ok -> {
            usuario.setText(""); contrasena.setText(""); tarjetas.show(contenido, "login");
        }));
        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(salir, BorderLayout.EAST);
        root.add(encabezado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(12, 12));
        centro.setOpaque(false);
        JPanel acciones = new JPanel(new GridBagLayout());
        acciones.setBackground(Color.WHITE);
        acciones.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(218, 228, 238)), BorderFactory.createEmptyBorder(15, 18, 15, 18)));
        JLabel lCuenta = new JLabel("Cuenta (8 dígitos)");
        lCuenta.setLabelFor(cuenta);
        JLabel lImporte = new JLabel("Importe del depósito");
        lImporte.setLabelFor(importe);
        cuenta.getAccessibleContext().setAccessibleName("Cuenta de ocho dígitos");
        importe.getAccessibleContext().setAccessibleName("Importe en dólares, hasta dos decimales");
        JButton consultar = boton("Consultar movimientos", true);
        consultar.addActionListener(e -> ejecutar(() -> controlador.consultarMovimientos(cuenta.getText().strip()), this::mostrarMovimientos));
        JButton depositar = boton("Registrar depósito", false);
        depositar.addActionListener(e -> depositar());
        JButton transferir = boton("Transferir", false);
        transferir.addActionListener(e -> transferir());
        GridBagConstraints g = restricciones();
        agregar(acciones, lCuenta, g, 0, 0, 1); agregar(acciones, cuenta, g, 1, 0, 1);
        agregar(acciones, lImporte, g, 0, 1, 1); agregar(acciones, importe, g, 1, 1, 1);
        agregar(acciones, consultar, g, 0, 2, 2); agregar(acciones, depositar, g, 0, 3, 2);
        agregar(acciones, new JLabel("Origen / destino / importe"), g, 0, 4, 1);
        JPanel camposTransferencia = new JPanel(); camposTransferencia.setOpaque(false);
        camposTransferencia.add(cuentaOrigen); camposTransferencia.add(cuentaDestino); camposTransferencia.add(importeTransferencia);
        agregar(acciones, camposTransferencia, g, 1, 4, 1); agregar(acciones, transferir, g, 0, 5, 2);
        centro.add(acciones, BorderLayout.NORTH);

        JTable tabla = new JTable(tablaModelo);
        tabla.setRowHeight(30);
        tabla.setFillsViewportHeight(true);
        tabla.getAccessibleContext().setAccessibleName("Movimientos de la cuenta");
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getAccessibleContext().setAccessibleName("Tabla de movimientos");
        centro.add(scroll, BorderLayout.CENTER);
        root.add(centro, BorderLayout.CENTER);
        estado.setForeground(AZUL);
        root.add(estado, BorderLayout.SOUTH);
        return root;
    }

    private void depositar() {
        BigDecimal valor;
        try { valor = new BigDecimal(importe.getText().strip().replace(',', '.')); }
        catch (NumberFormatException e) { aviso("Ingrese un importe válido."); return; }
        if (valor.signum() <= 0 || valor.scale() > 2) { aviso("El importe debe ser positivo y tener hasta dos decimales."); return; }
        ejecutar(() -> controlador.registrarDeposito(cuenta.getText().strip(), valor), ok -> {
            estado.setText(ok ? "Depósito registrado." : "No se pudo registrar el depósito.");
            if (ok) ejecutar(() -> controlador.consultarMovimientos(cuenta.getText().strip()), this::mostrarMovimientos);
        });
    }

    private void transferir() {
        BigDecimal valor;
        try { valor = new BigDecimal(importeTransferencia.getText().strip().replace(',', '.')); }
        catch (NumberFormatException e) { aviso("Ingrese un importe válido."); return; }
        String origen = cuentaOrigen.getText().strip(), destino = cuentaDestino.getText().strip();
        if (!origen.matches("[0-9]{8}") || !destino.matches("[0-9]{8}") || origen.equals(destino)
                || valor.signum() <= 0 || valor.scale() > 2) {
            aviso("Use dos cuentas distintas de ocho dígitos e importe positivo con hasta dos decimales."); return;
        }
        ejecutar(() -> controlador.transferir(origen, destino, valor), ok -> {
            estado.setText(ok ? "Transferencia registrada: débito y crédito confirmados."
                    : "Transferencia rechazada. Revise cuentas, moneda e importe.");
            if (ok) ejecutar(() -> controlador.consultarMovimientos(destino), this::mostrarMovimientos);
        });
    }

    private void mostrarMovimientos(List<Movimiento> movimientos) {
        tablaModelo.setRowCount(0);
        movimientos.forEach(m -> tablaModelo.addRow(new Object[]{m.numero(), m.cuenta(), m.fecha(), m.tipo(), m.accion(), m.importe()}));
        estado.setText(movimientos.isEmpty() ? "No hay movimientos para esa cuenta." : "Se cargaron " + movimientos.size() + " movimientos.");
    }

    private <T> void ejecutar(Llamada<T> llamada, Consumer<T> respuesta) {
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return llamada.ejecutar(); }
            @Override protected void done() {
                try { respuesta.accept(get()); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); aviso("Solicitud interrumpida."); }
                catch (ExecutionException e) { aviso("No se pudo comunicar con EurekaBank: " + e.getCause().getMessage()); }
            }
        }.execute();
    }

    private static JButton boton(String texto, boolean primario) {
        JButton b = new JButton(texto);
        b.setForeground(primario ? Color.WHITE : AZUL);
        b.setBackground(primario ? AZUL : new Color(226, 238, 249));
        b.setFocusPainted(true);
        b.setBorder(BorderFactory.createEmptyBorder(11, 16, 11, 16));
        return b;
    }

    private static GridBagConstraints restricciones() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 6, 7, 6); g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        return g;
    }

    private static void agregar(JPanel panel, java.awt.Component componente, GridBagConstraints base, int x, int y, int ancho) {
        GridBagConstraints g = (GridBagConstraints) base.clone(); g.gridx = x; g.gridy = y; g.gridwidth = ancho; panel.add(componente, g);
    }

    private void aviso(String texto) { JOptionPane.showMessageDialog(ventana, texto, "EurekaBank", JOptionPane.INFORMATION_MESSAGE); }
    @FunctionalInterface private interface Llamada<T> { T ejecutar() throws Exception; }

    private static final class Fondo extends JPanel {
        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setPaint(new GradientPaint(0, 0, new Color(9, 31, 68), getWidth(), getHeight(), new Color(15, 125, 145)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(255, 255, 255, 18)); g.fillOval(getWidth() - 230, -130, 360, 360);
            g.dispose();
        }
    }
}
