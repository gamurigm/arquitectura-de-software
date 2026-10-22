package ec.edu.monster.modelo;

import java.math.BigDecimal;

public final class Movimiento {
    public final String cuenta;
    public final int numero;
    public final String fecha;
    public final String tipo;
    public final String accion;
    public final BigDecimal importe;

    public Movimiento(String cuenta, int numero, String fecha, String tipo,
                      String accion, BigDecimal importe) {
        this.cuenta = cuenta; this.numero = numero; this.fecha = fecha;
        this.tipo = tipo; this.accion = accion; this.importe = importe;
    }

    @Override public String toString() {
        return fecha + "  ·  " + tipo + "  ·  " + accion + "  ·  USD " + importe;
    }
}
