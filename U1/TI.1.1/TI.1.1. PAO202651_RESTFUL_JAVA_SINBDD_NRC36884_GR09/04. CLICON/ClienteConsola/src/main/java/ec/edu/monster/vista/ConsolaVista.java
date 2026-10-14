package ec.edu.monster.vista;

import java.io.Console;
import java.util.Locale;
import java.util.Scanner;

public final class ConsolaVista {
    private static final String CIAN = "\u001B[1;96m";
    private static final String VERDE = "\u001B[92m";
    private static final String ROJO = "\u001B[91m";
    private static final String AMARILLO = "\u001B[93m";
    private static final String RESET = "\u001B[0m";

    private ConsolaVista() {
    }

    public static void mostrar(String texto) {
        String limpio = texto.trim().toLowerCase(Locale.ROOT);
        boolean destacado = limpio.startsWith("===") || limpio.startsWith("---")
                || limpio.contains("cliente de consola") || limpio.contains("menu principal")
                || limpio.contains("catalogo de unidades");
        String color = destacado ? CIAN
                : limpio.matches("^\\d+[).].*") || limpio.startsWith(">> ")
                        || limpio.startsWith("sesion iniciada") || limpio.startsWith("hasta luego")
                        || limpio.startsWith("resultado:") ? VERDE
                : limpio.startsWith("error") || limpio.contains("invalida")
                        || limpio.contains("fall") || limpio.contains("no se pudo")
                        || limpio.contains("conexión") ? ROJO
                : "";
        System.out.println(colorear(texto, color));
    }

    public static void escribir(String texto) {
        System.out.print(colorear(texto, AMARILLO));
    }

    public static String leerLinea(Scanner entrada) {
        return entrada.nextLine();
    }

    public static String leerClave() {
        Console consola = System.console();
        if (consola != null) {
            char[] clave = consola.readPassword("%s", colorear("Clave: ", AMARILLO));
            if (clave == null) {
                throw new IllegalStateException("Entrada cerrada.");
            }
            String valor = new String(clave);
            java.util.Arrays.fill(clave, '\0');
            return valor;
        }
        throw new IllegalStateException("Se requiere una terminal interactiva para ocultar la clave.");
    }

    private static String colorear(String texto, String color) {
        return System.console() != null && System.getenv("NO_COLOR") == null && !color.isEmpty()
                ? color + texto + RESET : texto;
    }
}
