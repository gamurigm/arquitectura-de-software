package ec.edu.monster.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Console;

public final class EntradaUtil {

    private static final Console CONSOLA = System.console();
    private static final BufferedReader ENTRADA = new BufferedReader(new InputStreamReader(System.in));
    private static final String AMARILLO = "\u001B[93m";
    private static final String ROJO = "\u001B[91m";
    private static final String RESET = "\u001B[0m";

    private EntradaUtil() {
    }

    public static String leerTextoNoVacio(String mensaje) {
        while (true) {
            String texto = leerLinea(mensaje).trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            mostrarError("Entrada invalida. El valor no puede quedar vacio.");
        }
    }

    public static String leerTextoConPredeterminado(String mensaje, String predeterminado) {
        String texto = leerLinea(mensaje).trim();
        return texto.isEmpty() ? predeterminado : texto;
    }

    public static String leerContrasenia(String mensaje) {
        return leerContrasenia(mensaje, null);
    }

    public static String leerContrasenia(String mensaje, String predeterminada) {
        if (CONSOLA == null) {
            throw new IllegalStateException("Ejecute el cliente desde una consola interactiva para ocultar la contrasena.");
        }
        while (true) {
            char[] contrasenia = CONSOLA.readPassword("%s", colorear(mensaje, AMARILLO));
            if (contrasenia == null) {
                throw new IllegalStateException("No se pudo leer la contrasena desde la consola.");
            }
            String valor = new String(contrasenia).trim();
            if (valor.isEmpty() && predeterminada != null) {
                return predeterminada;
            }
            if (!valor.isEmpty()) {
                return valor;
            }
            mostrarError("Entrada invalida. La contrasena no puede quedar vacia.");
        }
    }

    public static int leerEntero(String mensaje) {
        while (true) {
            String valor = leerLinea(mensaje).trim();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException ex) {
                mostrarError("Entrada invalida. Debe ingresar un numero entero.");
            }
        }
    }

    public static double leerDecimal(String mensaje) {
        while (true) {
            String valor = leerLinea(mensaje).trim().replace(',', '.');
            try {
                double numero = Double.parseDouble(valor);
                if (Double.isFinite(numero)) {
                    return numero;
                }
            } catch (NumberFormatException ex) {
            }
            mostrarError("Entrada invalida. Debe ingresar un numero decimal finito.");
        }
    }

    private static String leerLinea(String mensaje) {
        try {
            if (CONSOLA != null) {
                String linea = CONSOLA.readLine("%s", colorear(mensaje, AMARILLO));
                if (linea == null) {
                    throw new IllegalStateException("No hay mas entrada disponible.");
                }
                return linea;
            }
            System.out.print(colorear(mensaje, AMARILLO));
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

    private static void mostrarError(String mensaje) {
        System.err.println(colorear(mensaje, ROJO));
    }

    private static String colorear(String texto, String color) {
        return CONSOLA != null && System.getenv("NO_COLOR") == null ? color + texto + RESET : texto;
    }
}

