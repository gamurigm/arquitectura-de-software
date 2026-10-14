package ec.edu.monster.modelo;

/** Respuesta del endpoint /api/auth/login. */
public class RespuestaToken {

    private boolean exito;
    private String token;
    private String usuario;
    private String tipo;
    private long expiraEnSegundos;
    private String mensaje;

    public boolean esExitoso() {
        return exito;
    }

    public String obtenerToken() {
        return token;
    }

    public String obtenerUsuario() {
        return usuario;
    }

    public String obtenerTipo() {
        return tipo;
    }

    public long obtenerSegundosExpiracion() {
        return expiraEnSegundos;
    }

    public String obtenerMensaje() {
        return mensaje;
    }
}
