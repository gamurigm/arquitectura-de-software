package ec.edu.monster.climov.modelo;

/** Cuerpo de la peticion de login. */
public class LoginRequest {

    private final String usuario;
    private final String clave;

    public LoginRequest(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getClave() {
        return clave;
    }
}
