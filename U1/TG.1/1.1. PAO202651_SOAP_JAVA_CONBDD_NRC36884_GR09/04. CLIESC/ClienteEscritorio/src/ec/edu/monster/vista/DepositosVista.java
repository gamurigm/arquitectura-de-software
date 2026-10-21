package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class DepositosVista extends JDialog {
    private final JTextField cuenta = new JTextField(20);
    private final JTextField importe = new JTextField(20);
    private final JButton depositar = new JButton("Registrar depósito");
    private final JLabel estado = new JLabel("Ingrese la cuenta y el importe del depósito.");

    public DepositosVista(JFrame propietario) {
        super(propietario, "EurekaBank GR09 · Depósitos", true);
        JPanel contenido = new JPanel(new BorderLayout(16, 20));
        contenido.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        JPanel campos = new JPanel(new GridLayout(0, 1, 6, 8));
        JLabel etiquetaCuenta = new JLabel("Número de cuenta");
        etiquetaCuenta.setLabelFor(cuenta);
        JLabel etiquetaImporte = new JLabel("Importe (hasta dos decimales)");
        etiquetaImporte.setLabelFor(importe);
        cuenta.setName("cuenta");
        importe.setName("importe");
        depositar.setName("depositar");
        estado.setName("estadoDeposito");
        campos.add(etiquetaCuenta);
        campos.add(cuenta);
        campos.add(etiquetaImporte);
        campos.add(importe);
        campos.add(depositar);
        contenido.add(campos, BorderLayout.CENTER);
        contenido.add(estado, BorderLayout.SOUTH);
        setContentPane(contenido);
        getRootPane().setDefaultButton(depositar);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(propietario);
    }

    public String cuenta() { return cuenta.getText(); }
    public String importe() { return importe.getText(); }
    public void limpiarImporte() { importe.setText(""); }
    public void alDepositar(Runnable accion) { depositar.addActionListener(evento -> accion.run()); }
    public void mostrarMensaje(String mensaje) { estado.setText(mensaje); pack(); }

    public void ocupada(boolean ocupada) {
        depositar.setEnabled(!ocupada);
        cuenta.setEnabled(!ocupada);
        importe.setEnabled(!ocupada);
        setDefaultCloseOperation(ocupada ? DO_NOTHING_ON_CLOSE : DISPOSE_ON_CLOSE);
    }
}
