package ec.edu.monster.climov.api;

/** Respuesta del endpoint /api/auth/login. */
public class TokenResponse {

    private boolean exito;
    private String token;
    private String usuario;
    private String tipo;
    private long expiraEnSegundos;
    private String mensaje;

    public boolean isExito() {
        return exito;
    }

    public String getToken() {
        return token;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getTipo() {
        return tipo;
    }

    public long getExpiraEnSegundos() {
        return expiraEnSegundos;
    }

    public String getMensaje() {
        return mensaje;
    }
}
