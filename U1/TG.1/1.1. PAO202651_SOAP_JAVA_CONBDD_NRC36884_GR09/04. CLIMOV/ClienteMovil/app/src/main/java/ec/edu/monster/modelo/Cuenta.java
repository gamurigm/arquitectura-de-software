package ec.edu.monster.modelo;

import java.math.BigDecimal;

public record Cuenta(String numero, String moneda, BigDecimal saldo) {
    @Override public String toString() { return numero + " · " + moneda + " " + saldo; }
}
