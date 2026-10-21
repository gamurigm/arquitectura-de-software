package ec.edu.monster.vista;

import ec.edu.monster.controlador.EurekaController;
import ec.edu.monster.controlador.Login;
import ec.edu.monster.modelo.Movimiento;
import jakarta.xml.ws.WebServiceException;
import java.io.Console;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class MenuPrincipal {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String REINICIO = "\u001B[0m";
    private final Login login;
    private final EurekaController controlador;
    private boolean colores;

    public MenuPrincipal(Login login, EurekaController controlador) {
        this.login = login;
        this.controlador = controlador;
    }

    public void iniciar() {
        try (Scanner entrada = new Scanner(System.in)) {
            Console consola = System.console();
            colores = consola != null;
            if (!autenticar(entrada, consola)) {
                return;
            }
            mostrarMenu(entrada, consola);
        } catch (WebServiceException e) {
            System.err.println("No se pudo comunicar con EurekaBank: " + e.getMessage());
        }
    }

    private boolean autenticar(Scanner entrada, Console consola) {
        String usuario = leerLinea(entrada, consola, "Usuario: ");
        if (usuario == null || usuario.isBlank()) {
            System.out.println("Usuario vacío.");
            return false;
        }

        char[] contrasena;
        if (consola == null) {
            String linea = leerLinea(entrada, null, "Contraseña: ");
            if (linea == null) {
                return false;
            }
            contrasena = linea.toCharArray();
        } else {
            contrasena = consola.readPassword("Contraseña: ");
            if (contrasena == null) {
                return false;
            }
        }

        try {
            if (login.iniciarSesion(usuario.strip(), new String(contrasena))) {
                System.out.println(estilo("  Sesión iniciada.", "32"));
                return true;
            }
                System.out.println(estilo("  Usuario o contraseña incorrectos.", "31"));
            return false;
        } finally {
            Arrays.fill(contrasena, '\0');
        }
    }

    private void mostrarMenu(Scanner entrada, Console consola) {
        while (true) {
            System.out.println("\n" + estilo("  EUREKABANK  /  BANCA PERSONAL", "1;96"));
            System.out.println(estilo("  ──────────────────────────────────", "36"));
            System.out.println("  " + estilo("1", "96") + "  Consultar movimientos");
            System.out.println("  " + estilo("2", "96") + "  Registrar depósito");
            System.out.println("  " + estilo("3", "96") + "  Cerrar sesión");
            String opcion = leerLinea(entrada, consola, "Opción: ");
            if (opcion == null || "3".equals(opcion.strip())) {
                return;
            }

            switch (opcion.strip()) {
                case "1" -> consultarMovimientos(entrada, consola);
                case "2" -> registrarDeposito(entrada, consola);
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private void consultarMovimientos(Scanner entrada, Console consola) {
        String cuenta = leerLinea(entrada, consola, "Número de cuenta: ");
        if (cuenta == null || cuenta.isBlank()) {
            System.out.println("Cuenta vacía.");
            return;
        }

        List<Movimiento> movimientos = controlador.consultarMovimientos(cuenta.strip());
        if (movimientos.isEmpty()) {
            System.out.println("No hay movimientos para esa cuenta.");
            return;
        }

        System.out.println(estilo("Movimientos de la cuenta " + cuenta.strip(), "1;96"));
        System.out.printf("%-12s %-8s %-18s %-12s %-12s %12s%n",
                "CUENTA", "N°", "FECHA", "TIPO", "ACCIÓN", "IMPORTE");
        for (Movimiento movimiento : movimientos) {
            String fecha = movimiento.fecha() == null ? "-" : movimiento.fecha().format(FORMATO_FECHA);
            String importe = movimiento.importe() == null ? "-" : movimiento.importe().toPlainString();
            System.out.printf("%-12s %-8d %-18s %-12s %-12s %12s%n",
                    movimiento.cuenta(), movimiento.numero(), fecha, movimiento.tipo(), movimiento.accion(), importe);
        }
    }

    private void registrarDeposito(Scanner entrada, Console consola) {
        String cuenta = leerLinea(entrada, consola, "Número de cuenta: ");
        if (cuenta == null || cuenta.isBlank()) {
            System.out.println("Cuenta vacía.");
            return;
        }

        String textoImporte = leerLinea(entrada, consola, "Importe: ");
        if (textoImporte == null) {
            return;
        }

        BigDecimal importe;
        try {
            importe = new BigDecimal(textoImporte.strip().replace(',', '.'));
        } catch (NumberFormatException e) {
            System.out.println("Importe inválido.");
            return;
        }

        System.out.println(controlador.registrarDeposito(cuenta.strip(), importe)
                ? estilo("  Depósito registrado.", "32")
                : estilo("  No se pudo registrar el depósito.", "31"));
    }

    private String leerLinea(Scanner entrada, Console consola, String mensaje) {
        if (consola != null) {
            return consola.readLine(estilo(mensaje, "96"));
        }
        System.out.print(mensaje);
        return entrada.hasNextLine() ? entrada.nextLine() : null;
    }

    private String estilo(String texto, String codigo) {
        return colores ? "\u001B[" + codigo + "m" + texto + REINICIO : texto;
    }
}
