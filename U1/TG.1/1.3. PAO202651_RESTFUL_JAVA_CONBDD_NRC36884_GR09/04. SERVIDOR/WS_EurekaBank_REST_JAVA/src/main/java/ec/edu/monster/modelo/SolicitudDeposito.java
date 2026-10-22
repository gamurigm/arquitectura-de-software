package ec.edu.monster.modelo;

import java.math.BigDecimal;

public class SolicitudDeposito {
    private String cuenta;
    private BigDecimal importe;
    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
}
