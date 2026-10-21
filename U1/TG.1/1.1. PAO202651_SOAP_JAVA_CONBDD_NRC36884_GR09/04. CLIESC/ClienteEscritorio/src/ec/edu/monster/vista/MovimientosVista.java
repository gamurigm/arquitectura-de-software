package ec.edu.monster.vista;

import ec.edu.monster.modelo.Movimiento;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public final class MovimientosVista extends JDialog {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final JTextField cuenta = new JTextField(12);
    private final JButton buscar = new JButton("Buscar");
    private final JLabel estado = new JLabel("Ingrese la cuenta que desea consultar.");
    private final DefaultTableModel datos = new DefaultTableModel(
            new String[] {"N.º", "Cuenta", "Fecha", "Tipo", "Acción", "Importe"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) { return false; }
    };

    public MovimientosVista(JFrame propietario) {
        super(propietario, "EurekaBank GR09 · Movimientos", true);
        JPanel contenido = new JPanel(new BorderLayout(12, 16));
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel filtro = new JPanel();
        JLabel etiqueta = new JLabel("Número de cuenta");
        etiqueta.setLabelFor(cuenta);
        cuenta.setName("cuenta");
        buscar.setName("buscar");
        filtro.add(etiqueta);
        filtro.add(cuenta);
        filtro.add(buscar);
        JTable tabla = new JTable(datos);
        tabla.setName("movimientos");
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(26);
        tabla.getAccessibleContext().setAccessibleName("Movimientos de la cuenta");
        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setPreferredSize(new Dimension(760, 300));
        contenido.add(filtro, BorderLayout.NORTH);
        contenido.add(desplazamiento, BorderLayout.CENTER);
        contenido.add(estado, BorderLayout.SOUTH);
        setContentPane(contenido);
        getRootPane().setDefaultButton(buscar);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(propietario);
    }

    public String cuenta() { return cuenta.getText(); }
    public void alBuscar(Runnable accion) { buscar.addActionListener(evento -> accion.run()); }
    public void mostrarMensaje(String mensaje) { estado.setText(mensaje); }
    public void limpiar() { datos.setRowCount(0); }

    public void mostrarMovimientos(List<Movimiento> movimientos) {
        limpiar();
        for (Movimiento movimiento : movimientos) {
            datos.addRow(new Object[] {movimiento.numero(), movimiento.cuenta(),
                    movimiento.fecha() == null ? "-" : movimiento.fecha().format(FECHA),
                    movimiento.tipo(), movimiento.accion(),
                    movimiento.importe() == null ? "-" : movimiento.importe().toPlainString()});
        }
        mostrarMensaje(movimientos.isEmpty() ? "No hay movimientos para esta cuenta."
                : movimientos.size() + " movimiento(s) encontrado(s).");
    }

    public void ocupada(boolean ocupada) {
        buscar.setEnabled(!ocupada);
        cuenta.setEnabled(!ocupada);
        setDefaultCloseOperation(ocupada ? DO_NOTHING_ON_CLOSE : DISPOSE_ON_CLOSE);
    }
}
