package ec.edu.monster.vista;

public final class MensajesVista {

    private static final String CIAN = "\u001B[1;96m";
    private static final String VERDE = "\u001B[92m";
    private static final String ROJO = "\u001B[91m";
    private static final String RESET = "\u001B[0m";

    public void mostrarTitulo(String texto) {
        System.out.println();
        imprimir("╭────────────────────────────────────────────╮", CIAN);
        imprimir(texto, CIAN);
        imprimir("╰────────────────────────────────────────────╯", CIAN);
    }

    public void mostrarBanner(String texto) {
        imprimir(texto, CIAN);
    }

    public void mostrarMensaje(String mensaje) {
        String limpio = mensaje.toLowerCase(java.util.Locale.ROOT);
        String color = mensaje.matches("^\\d+\\..*") || limpio.startsWith("resultado:")
                || limpio.startsWith("inicio de sesion exitoso")
                || limpio.startsWith("sesion finalizada") || limpio.startsWith("saliendo")
                ? VERDE : "";
        imprimir(mensaje, color);
    }

    public void mostrarError(String mensaje) {
        imprimir("Error: " + mensaje, ROJO);
    }

    private static void imprimir(String texto, String color) {
        boolean usarColor = System.console() != null && System.getenv("NO_COLOR") == null;
        System.out.println(usarColor && !color.isEmpty() ? color + texto + RESET : texto);
    }
}
