package ec.edu.monster.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Console;

public final class EntradaUtil {

    private static final Console CONSOLA = System.console();
    private static final BufferedReader ENTRADA = new BufferedReader(new InputStreamReader(System.in));

    private EntradaUtil() {
    }

    public static String leerTextoNoVacio(String mensaje) {
        while (true) {
            String texto = leerLinea(mensaje).trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.err.println("Entrada invalida. El valor no puede quedar vacio.");
        }
    }

    public static String leerContrasenia(String mensaje) {
        if (CONSOLA == null) {
            throw new IllegalStateException("Ejecute el cliente desde una consola interactiva para ocultar la contrasena.");
        }
        char[] contrasenia = CONSOLA.readPassword("%s", mensaje);
        if (contrasenia == null) {
            throw new IllegalStateException("No se pudo leer la contrasena desde la consola.");
        }
        return new String(contrasenia).trim();
    }

    public static int leerEntero(String mensaje) {
        while (true) {
            String valor = leerLinea(mensaje).trim();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException ex) {
                System.err.println("Entrada invalida. Debe ingresar un numero entero.");
            }
        }
    }

    public static double leerDecimal(String mensaje) {
        while (true) {
            String valor = leerLinea(mensaje).trim().replace(',', '.');
            try {
                return Double.parseDouble(valor);
            } catch (NumberFormatException ex) {
                System.err.println("Entrada invalida. Debe ingresar un numero decimal.");
            }
        }
    }

    private static String leerLinea(String mensaje) {
        try {
            if (CONSOLA != null) {
                String linea = CONSOLA.readLine("%s", mensaje);
                if (linea == null) {
                    throw new IllegalStateException("No hay mas entrada disponible.");
                }
                return linea;
            }
            System.out.print(mensaje);
            System.out.flush();
            String linea = ENTRADA.readLine();
            if (linea == null) {
                throw new IllegalStateException("No hay mas entrada disponible.");
            }
            return linea;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer la entrada de la consola.", ex);
        }
    }
}

