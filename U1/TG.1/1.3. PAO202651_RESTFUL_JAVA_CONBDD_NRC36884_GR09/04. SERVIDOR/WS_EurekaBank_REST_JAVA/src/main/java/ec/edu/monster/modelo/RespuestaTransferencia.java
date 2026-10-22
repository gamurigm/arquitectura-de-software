package ec.edu.monster.modelo;

public class RespuestaTransferencia {
    private boolean registrada;
    public RespuestaTransferencia() { }
    public RespuestaTransferencia(boolean registrada) { this.registrada = registrada; }
    public boolean isRegistrada() { return registrada; }
    public void setRegistrada(boolean registrada) { this.registrada = registrada; }
}
