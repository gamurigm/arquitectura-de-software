package ec.edu.monster.vista;

import ec.edu.monster.controlador.ControladorConsola;
import ec.edu.monster.modelo.Movimiento;
import java.io.Console;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class ConsolaVista {
    private final ControladorConsola controlador;

    public ConsolaVista(ControladorConsola controlador) { this.controlador = controlador; }

    public void iniciar() {
        try (Scanner entrada = new Scanner(System.in)) {
            Console consola = System.console();
            if (!autenticar(entrada, consola)) return;
            while (controlador.estaAutenticado()) {
                System.out.println("\nEUREKABANK  |  Bienvenido/a, " + controlador.usuarioActual());
                System.out.println("1. Consultar movimientos\n2. Registrar depósito\n3. Transferir\n4. Cerrar sesión");
                switch (leer(entrada, consola, "Opción: ")) {
                    case "1" -> consultar(entrada, consola);
                    case "2" -> depositar(entrada, consola);
                    case "3" -> transferir(entrada, consola);
                    case "4" -> { try { controlador.cerrarSesion(); } catch (IOException | InterruptedException e) { fallo(e); } System.out.println("Sesión cerrada."); }
                    default -> System.out.println("Seleccione una opción válida.");
                }
            }
        }
    }

    private boolean autenticar(Scanner entrada, Console consola) {
        String usuario = leer(entrada, consola, "Usuario: ").strip();
        char[] clave = consola == null ? leer(entrada, null, "Contraseña: ").toCharArray() : consola.readPassword("Contraseña: ");
        if (clave == null) return false;
        try {
            if (controlador.iniciarSesion(usuario, new String(clave))) return true;
            System.out.println("Credenciales incorrectas o cuenta sin asociación habilitada.");
            return false;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            System.out.println("No se pudo comunicar con EurekaBank: " + e.getMessage());
            return false;
        } finally { Arrays.fill(clave, '\0'); }
    }

    private void consultar(Scanner entrada, Console consola) {
        String cuenta = leer(entrada, consola, "Número de cuenta (8 dígitos): ").strip();
        try {
            List<Movimiento> movimientos = controlador.consultarMovimientos(cuenta);
            if (movimientos.isEmpty()) { System.out.println("No hay movimientos para esa cuenta."); return; }
            System.out.printf("%-8s %-8s %-12s %-24s %-10s %12s%n", "NÚMERO", "CUENTA", "FECHA", "TIPO", "ACCIÓN", "IMPORTE");
            for (Movimiento m : movimientos) {
                System.out.printf("%-8d %-8s %-12s %-24s %-10s %12s%n", m.numero(), m.cuenta(), m.fecha(), m.tipo(), m.accion(), m.importe());
            }
        } catch (IOException | InterruptedException e) { fallo(e); }
    }

    private void depositar(Scanner entrada, Console consola) {
        String cuenta = leer(entrada, consola, "Número de cuenta (8 dígitos): ").strip();
        BigDecimal importe;
        try { importe = new BigDecimal(leer(entrada, consola, "Importe: ").strip().replace(',', '.')); }
        catch (NumberFormatException e) { System.out.println("Ingrese un importe decimal válido."); return; }
        if (importe.signum() <= 0 || importe.scale() > 2) { System.out.println("El importe debe ser positivo y tener hasta dos decimales."); return; }
        try { System.out.println(controlador.depositar(cuenta, importe) ? "Depósito registrado." : "No se pudo registrar el depósito."); }
        catch (IOException | InterruptedException e) { fallo(e); }
    }

    private void transferir(Scanner entrada, Console consola) {
        String origen = leer(entrada, consola, "Cuenta origen: ").strip();
        String destino = leer(entrada, consola, "Cuenta destino: ").strip();
        BigDecimal importe;
        try { importe = new BigDecimal(leer(entrada, consola, "Importe: ").strip().replace(',', '.')); }
        catch (NumberFormatException e) { System.out.println("Ingrese un importe decimal válido."); return; }
        if (origen.equals(destino) || importe.signum() <= 0 || importe.scale() > 2) {
            System.out.println("Cuentas distintas e importe positivo con hasta dos decimales requeridos."); return;
        }
        String clave = java.util.UUID.randomUUID().toString();
        try {
            System.out.println(controlador.transferir(origen, destino, importe, clave)
                    ? "Transferencia registrada: débito y crédito confirmados."
                    : "Transferencia rechazada. Revise cuentas, moneda, saldo e importe.");
        } catch (IOException | InterruptedException e) { fallo(e); }
    }

    private String leer(Scanner entrada, Console consola, String prompt) {
        if (consola != null) { String valor = consola.readLine(prompt); return valor == null ? "" : valor; }
        System.out.print(prompt);
        return entrada.hasNextLine() ? entrada.nextLine() : "";
    }

    private void fallo(Exception e) {
        if (e instanceof InterruptedException) Thread.currentThread().interrupt();
        System.out.println("No se pudo completar la solicitud: " + e.getMessage());
    }
}
