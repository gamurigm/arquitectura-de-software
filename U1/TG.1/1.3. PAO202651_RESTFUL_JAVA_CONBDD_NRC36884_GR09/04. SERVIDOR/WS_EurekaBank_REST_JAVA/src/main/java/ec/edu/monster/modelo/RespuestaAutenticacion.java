package ec.edu.monster.modelo;

public class RespuestaAutenticacion {
    private boolean autenticado;
    private String token;
    public RespuestaAutenticacion() { }
    public RespuestaAutenticacion(boolean autenticado) { this(autenticado, null); }
    public RespuestaAutenticacion(boolean autenticado, String token) { this.autenticado = autenticado; this.token = token; }
    public boolean isAutenticado() { return autenticado; }
    public void setAutenticado(boolean autenticado) { this.autenticado = autenticado; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
