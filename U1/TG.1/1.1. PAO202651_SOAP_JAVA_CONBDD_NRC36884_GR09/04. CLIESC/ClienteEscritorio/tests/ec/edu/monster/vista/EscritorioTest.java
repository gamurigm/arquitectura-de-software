package ec.edu.monster.vista;

import ec.edu.monster.SoapSimulado;
import ec.edu.monster.controlador.EurekaControlador;
import ec.edu.monster.service.EurekaService;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.BooleanSupplier;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import static org.junit.jupiter.api.Assertions.*;

@EnabledIfSystemProperty(named = "eurekabank.ui.tests", matches = "true")
class EscritorioTest {
    @Test
    void navegaSinBloquearLaInterfazNiDuplicarDepositos() throws Exception {
        try (SoapSimulado banco = new SoapSimulado()) {
            EurekaControlador controlador = new EurekaControlador(new EurekaService(banco.endpoint()));
            try {
                edt(() -> { controlador.iniciar(); return null; });
                edt(() -> {
                    capturar("login", componente("iniciarSesion", JButton.class));
                    componente("usuario", JTextField.class).setText("usuario-prueba");
                    componente("contrasena", JPasswordField.class).setText("clave-prueba");
                    componente("iniciarSesion", JButton.class).doClick();
                    return null;
                });
                esperar(() -> componente("verMovimientos", JButton.class) != null);
                edt(() -> { capturar("menu", componente("verMovimientos", JButton.class)); return null; });
                SwingUtilities.invokeLater(() -> componente("verMovimientos", JButton.class).doClick());
                esperar(() -> componente("movimientos", JTable.class) != null);
                banco.demora = 500;
                edt(() -> {
                    componente("cuenta", JTextField.class).setText("00100001");
                    componente("buscar", JButton.class).doClick();
                    assertFalse(componente("buscar", JButton.class).isEnabled());
                    return null;
                });
                // Esta llamada debe volver mientras la petición sigue pendiente.
                assertFalse(edt(() -> componente("buscar", JButton.class).isEnabled()));
                esperar(() -> componente("movimientos", JTable.class).getRowCount() == 1);
                edt(() -> {
                    JTable tabla = componente("movimientos", JTable.class);
                    assertFalse(tabla.isCellEditable(0, 0));
                    assertEquals("00100001", tabla.getValueAt(0, 1));
                    capturar("movimientos", tabla);
                    ((JDialog) SwingUtilities.getWindowAncestor(tabla)).dispose();
                    return null;
                });
                SwingUtilities.invokeLater(() -> componente("verDepositos", JButton.class).doClick());
                esperar(() -> componente("depositar", JButton.class) != null);
                int anteriores = banco.llamadas.get();
                edt(() -> {
                    componente("cuenta", JTextField.class).setText("00100001");
                    componente("importe", JTextField.class).setText("1.234");
                    componente("depositar", JButton.class).doClick();
                    return null;
                });
                esperar(() -> componente("estadoDeposito", JLabel.class).getText().contains("dos decimales"));
                assertEquals(anteriores, banco.llamadas.get());
                edt(() -> {
                    componente("importe", JTextField.class).setText("0,10");
                    JButton boton = componente("depositar", JButton.class);
                    boton.doClick();
                    boton.doClick();
                    assertFalse(boton.isEnabled());
                    return null;
                });
                esperar(() -> componente("estadoDeposito", JLabel.class).getText().contains("registrado"));
                assertEquals(anteriores + 1, banco.llamadas.get());
                edt(() -> {
                    JButton boton = componente("depositar", JButton.class);
                    capturar("depositos", boton);
                    ((JDialog) SwingUtilities.getWindowAncestor(boton)).dispose();
                    componente("cerrarSesion", JButton.class).doClick();
                    return null;
                });
                esperar(() -> componente("iniciarSesion", JButton.class) != null);
                assertThrows(IllegalStateException.class,
                        () -> controlador.consultarMovimientos("00100001"));
            } finally {
                edt(() -> { for (Window ventana : Window.getWindows()) ventana.dispose(); return null; });
            }
        }
    }

    private static <T> T edt(Callable<T> tarea) throws Exception {
        FutureTask<T> futuro = new FutureTask<>(tarea);
        SwingUtilities.invokeAndWait(futuro);
        return futuro.get();
    }

    private static void esperar(BooleanSupplier condicion) throws Exception {
        long limite = System.nanoTime() + 5_000_000_000L;
        while (System.nanoTime() < limite) {
            if (edt(condicion::getAsBoolean)) return;
            Thread.sleep(25);
        }
        fail("La interfaz no alcanzó el estado esperado.");
    }

    private static <T extends Component> T componente(String nombre, Class<T> tipo) {
        for (Window ventana : Window.getWindows()) {
            if (ventana.isShowing()) {
                T encontrado = buscar(ventana, nombre, tipo);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private static <T extends Component> T buscar(Component componente, String nombre, Class<T> tipo) {
        if (nombre.equals(componente.getName()) && tipo.isInstance(componente)) return tipo.cast(componente);
        if (componente instanceof Container contenedor) {
            for (Component hijo : contenedor.getComponents()) {
                T encontrado = buscar(hijo, nombre, tipo);
                if (encontrado != null) return encontrado;
            }
        }
        return null;
    }

    private static void capturar(String nombre, Component componente) throws Exception {
        Window ventana = SwingUtilities.getWindowAncestor(componente);
        BufferedImage imagen = new BufferedImage(ventana.getWidth(), ventana.getHeight(), BufferedImage.TYPE_INT_RGB);
        var graficos = imagen.createGraphics();
        ventana.printAll(graficos);
        graficos.dispose();
        Path carpeta = Path.of("D:/tools/eurekabank-desktop-evidence");
        Files.createDirectories(carpeta);
        ImageIO.write(imagen, "png", carpeta.resolve(nombre + ".png").toFile());
    }
}
