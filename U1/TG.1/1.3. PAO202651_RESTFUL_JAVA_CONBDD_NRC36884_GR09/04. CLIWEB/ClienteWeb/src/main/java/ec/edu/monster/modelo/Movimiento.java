package ec.edu.monster.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Movimiento {
    private int numero;
    private String cuenta;
    private LocalDate fecha;
    private String tipo;
    private String accion;
    private BigDecimal importe;

    public Movimiento() { }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
}
