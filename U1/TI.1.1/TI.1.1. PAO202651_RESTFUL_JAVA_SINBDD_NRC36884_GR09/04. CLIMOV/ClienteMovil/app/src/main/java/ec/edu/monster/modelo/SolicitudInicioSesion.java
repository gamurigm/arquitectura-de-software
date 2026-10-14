package ec.edu.monster.modelo;

/** Solicitud de inicio de sesión. */
public class SolicitudInicioSesion {

    private final String usuario;
    private final String clave;

    public SolicitudInicioSesion(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public String obtenerUsuario() {
        return usuario;
    }

    public String obtenerClave() {
        return clave;
    }
}
