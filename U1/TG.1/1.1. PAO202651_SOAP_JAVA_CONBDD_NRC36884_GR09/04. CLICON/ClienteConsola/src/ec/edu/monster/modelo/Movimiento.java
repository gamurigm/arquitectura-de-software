package ec.edu.monster.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Movimiento(
        String cuenta,
        int numero,
        LocalDateTime fecha,
        String tipo,
        String accion,
        BigDecimal importe) {
}
