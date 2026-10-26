package ec.edu.monster.modelo;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
public record Cuenta(String numero, String moneda, String monedaNombre, BigDecimal saldo) {
    public String nombreMoneda() { return "Dolares".equalsIgnoreCase(monedaNombre) ? "Dólares" : monedaNombre == null || monedaNombre.isBlank() ? "Moneda " + moneda : monedaNombre; }
    public String simbolo() { return "01".equals(moneda) ? "S/" : "02".equals(moneda) ? "$" : moneda; }
    public String saldoFormateado() { var formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-EC")); formato.setMinimumFractionDigits(2); formato.setMaximumFractionDigits(2); return formato.format(saldo); }
    @Override public String toString() { return numero + "  |  " + nombreMoneda() + "  |  " + simbolo() + " " + saldoFormateado(); }
}
