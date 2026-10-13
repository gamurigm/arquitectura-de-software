package ec.edu.monster.seguridad;

public final class AdministradorCredenciales {

    private static final String USUARIO = "Monster";
    private static volatile String contrasenia = "Monster9";

    private AdministradorCredenciales() {
    }

    public static boolean validarCredenciales(String usuario, String clave) {
        return USUARIO.equals(usuario) && contrasenia.equals(clave);
    }

    public static synchronized void cambiarContrasenia(String actual, String nueva) {
        if (!contrasenia.equals(actual)) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        if (nueva == null || nueva.isBlank()) {
            throw new IllegalArgumentException("La contraseña nueva no puede estar vacía");
        }
        contrasenia = nueva;
    }
}