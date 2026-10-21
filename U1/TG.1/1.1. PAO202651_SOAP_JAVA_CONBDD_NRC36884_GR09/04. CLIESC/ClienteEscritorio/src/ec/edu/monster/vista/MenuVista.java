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

public final class MenuVista extends JFrame {
    private final JButton movimientos = new JButton("Consultar movimientos");
    private final JButton depositos = new JButton("Registrar depósito");
    private final JButton salir = new JButton("Cerrar sesión");

    public MenuVista(String usuario) {
        super("EurekaBank GR09 · Menú");
        TemaEureka.Tarjeta contenido = new TemaEureka.Tarjeta(new BorderLayout(16, 20));
        JLabel titulo = new JLabel("EUREKABANK", JLabel.CENTER);
        TemaEureka.titulo(titulo, 27f);
        contenido.add(titulo, BorderLayout.NORTH);
        JPanel opciones = new JPanel(new GridLayout(0, 1, 8, 12));
        opciones.setOpaque(false);
        JLabel sesion = new JLabel("Hola, " + usuario);
        TemaEureka.titulo(sesion, 17f);
        opciones.add(sesion);
        movimientos.setName("verMovimientos");
        depositos.setName("verDepositos");
        salir.setName("cerrarSesion");
        TemaEureka.boton(movimientos, true);
        TemaEureka.boton(depositos, false);
        TemaEureka.boton(salir, false);
        opciones.add(movimientos);
        opciones.add(depositos);
        opciones.add(salir);
        contenido.add(opciones, BorderLayout.CENTER);
        TemaEureka.ventana(this, contenido, 430);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    public void alConsultar(Runnable accion) { movimientos.addActionListener(evento -> accion.run()); }
    public void alDepositar(Runnable accion) { depositos.addActionListener(evento -> accion.run()); }
    public void alSalir(Runnable accion) { salir.addActionListener(evento -> accion.run()); }
}
