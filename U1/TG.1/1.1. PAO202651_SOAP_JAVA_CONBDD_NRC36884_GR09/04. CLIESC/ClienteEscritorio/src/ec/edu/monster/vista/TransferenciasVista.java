package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public final class TransferenciasVista extends JDialog {
    private final JTextField origen = new JTextField(20);
    private final JTextField destino = new JTextField(20);
    private final JTextField importe = new JTextField(20);
    private final JButton transferir = new JButton("Confirmar transferencia");
    private final JLabel estado = new JLabel("El débito y el crédito se registran juntos.");

    public TransferenciasVista(JFrame propietario) {
        super(propietario, "EurekaBank GR09 · Transferencias", true);
        TemaEureka.Tarjeta contenido = new TemaEureka.Tarjeta(new BorderLayout(16, 20));
        JPanel campos = new JPanel(new GridLayout(0, 1, 6, 8));
        campos.setOpaque(false);
        agregar(campos, "Cuenta origen", origen);
        agregar(campos, "Cuenta destino", destino);
        agregar(campos, "Importe (hasta dos decimales)", importe);
        TemaEureka.boton(transferir, true);
        transferir.setName("transferir");
        campos.add(transferir);
        contenido.add(campos, BorderLayout.CENTER);
        contenido.add(estado, BorderLayout.SOUTH);
        TemaEureka.ventana(this, contenido, 430);
        getRootPane().setDefaultButton(transferir);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(propietario);
    }

    private static void agregar(JPanel panel, String texto, JTextField campo) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setLabelFor(campo);
        TemaEureka.etiqueta(etiqueta);
        TemaEureka.campo(campo);
        panel.add(etiqueta);
        panel.add(campo);
    }

    public String origen() { return origen.getText(); }
    public String destino() { return destino.getText(); }
    public String importe() { return importe.getText(); }
    public void limpiar() { origen.setText(""); destino.setText(""); importe.setText(""); }
    public void alTransferir(Runnable accion) { transferir.addActionListener(evento -> accion.run()); }
    public void mostrarMensaje(String mensaje) { estado.setText(mensaje); pack(); }
    public void ocupada(boolean ocupada) {
        transferir.setEnabled(!ocupada); origen.setEnabled(!ocupada); destino.setEnabled(!ocupada); importe.setEnabled(!ocupada);
        setDefaultCloseOperation(ocupada ? DO_NOTHING_ON_CLOSE : DISPOSE_ON_CLOSE);
    }
}
