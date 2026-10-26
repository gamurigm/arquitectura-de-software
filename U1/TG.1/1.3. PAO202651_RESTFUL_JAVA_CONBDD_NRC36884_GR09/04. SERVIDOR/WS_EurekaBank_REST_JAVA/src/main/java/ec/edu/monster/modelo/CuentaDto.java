package ec.edu.monster.modelo;

import java.math.BigDecimal;

public class CuentaDto {
    private String numero;
    private String moneda;
    private String monedaNombre;
    private BigDecimal saldo;

    public CuentaDto() { }
    public CuentaDto(String numero, String moneda, String monedaNombre, BigDecimal saldo) {
        this.numero = numero;
        this.moneda = moneda;
        this.monedaNombre = monedaNombre;
        this.saldo = saldo;
    }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getMonedaNombre() { return monedaNombre; }
    public void setMonedaNombre(String monedaNombre) { this.monedaNombre = monedaNombre; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}
