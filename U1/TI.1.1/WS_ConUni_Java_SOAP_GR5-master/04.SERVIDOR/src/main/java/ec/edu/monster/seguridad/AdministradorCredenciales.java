package ec.edu.monster.seguridad;

public final class AdministradorCredenciales {

    private static final String USUARIO = "Monster";
    private static String contrasenia = "Monster9";

    private AdministradorCredenciales() {
    }

    public static synchronized boolean validarCredenciales(String usuario, String contraseniaIngresada) {
        return USUARIO.equals(usuario) && contrasenia.equals(contraseniaIngresada);
    }

    public static synchronized void cambiarContrasenia(String contraseniaActual, String contraseniaNueva) {
        if (!contrasenia.equals(contraseniaActual)) {
            throw new RuntimeException("La contrasenia actual es incorrecta");
        }

        if (contraseniaNueva == null || contraseniaNueva.isBlank()) {
            throw new RuntimeException("La nueva contrasenia es obligatoria");
        }

        contrasenia = contraseniaNueva;
    }
}
