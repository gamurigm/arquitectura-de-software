package ec.edu.monster.modelo;

public class RespuestaDeposito {
    private boolean registrado;
    public RespuestaDeposito() { }
    public RespuestaDeposito(boolean registrado) { this.registrado = registrado; }
    public boolean isRegistrado() { return registrado; }
    public void setRegistrado(boolean registrado) { this.registrado = registrado; }
}
