package ec.edu.monster.modelo;

import java.math.BigDecimal;

public record Movimiento(int numero, String cuenta, String fecha, String tipo, String accion, BigDecimal importe) { }
