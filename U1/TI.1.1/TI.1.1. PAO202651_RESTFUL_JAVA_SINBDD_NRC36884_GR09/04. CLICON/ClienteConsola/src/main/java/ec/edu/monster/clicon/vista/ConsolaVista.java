package ec.edu.monster.clicon.vista;

import java.io.Console;
import java.util.Scanner;

public final class ConsolaVista {
    private ConsolaVista() {
    }

    public static void mostrar(String texto) {
        System.out.println(texto);
    }

    public static void escribir(String texto) {
        System.out.print(texto);
    }

    public static String leerLinea(Scanner entrada) {
        return entrada.nextLine();
    }

    public static String leerClave() {
        Console consola = System.console();
        if (consola != null) {
            char[] clave = consola.readPassword("Clave: ");
            if (clave == null) {
                throw new IllegalStateException("Entrada cerrada.");
            }
            String valor = new String(clave);
            java.util.Arrays.fill(clave, '\0');
            return valor;
        }
        throw new IllegalStateException("Se requiere una terminal interactiva para ocultar la clave.");
    }
}
