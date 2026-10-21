package ec.edu.monster.modelo;

public class MensajesModelo {

    public void mostrarTitulo(String texto) {
        System.out.println();
        System.out.println("==============================================");
        System.out.println(texto);
        System.out.println("==============================================");
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje) {
        System.err.println("Error: " + mensaje);
    }
}

