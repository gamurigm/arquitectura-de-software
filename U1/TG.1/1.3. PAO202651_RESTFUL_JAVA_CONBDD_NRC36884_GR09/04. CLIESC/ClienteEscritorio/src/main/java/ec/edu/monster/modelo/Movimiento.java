package ec.edu.monster.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Movimiento(int numero, String cuenta, LocalDate fecha, String tipo, String accion, BigDecimal importe) { }
