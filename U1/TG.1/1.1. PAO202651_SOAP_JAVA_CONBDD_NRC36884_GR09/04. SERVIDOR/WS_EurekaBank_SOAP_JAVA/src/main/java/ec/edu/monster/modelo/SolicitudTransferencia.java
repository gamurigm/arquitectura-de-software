package ec.edu.monster.modelo;

import java.math.BigDecimal;

public class SolicitudTransferencia {
    private String cuentaOrigen;
    private String cuentaDestino;
    private BigDecimal importe;
    private String claveOperacion;

    public String getCuentaOrigen() { return cuentaOrigen; }
    public void setCuentaOrigen(String cuentaOrigen) { this.cuentaOrigen = cuentaOrigen; }
    public String getCuentaDestino() { return cuentaDestino; }
    public void setCuentaDestino(String cuentaDestino) { this.cuentaDestino = cuentaDestino; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public String getClaveOperacion() { return claveOperacion; }
    public void setClaveOperacion(String claveOperacion) { this.claveOperacion = claveOperacion; }
}
