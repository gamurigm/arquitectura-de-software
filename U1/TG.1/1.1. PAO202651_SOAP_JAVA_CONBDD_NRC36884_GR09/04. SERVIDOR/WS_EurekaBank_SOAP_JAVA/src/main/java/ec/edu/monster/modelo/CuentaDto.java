package ec.edu.monster.modelo;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cuenta")
public class CuentaDto {
    private String numero;
    private String moneda;
    private BigDecimal saldo;

    public CuentaDto() { }
    public CuentaDto(String numero, String moneda, BigDecimal saldo) {
        this.numero = numero;
        this.moneda = moneda;
        this.saldo = saldo;
    }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}
