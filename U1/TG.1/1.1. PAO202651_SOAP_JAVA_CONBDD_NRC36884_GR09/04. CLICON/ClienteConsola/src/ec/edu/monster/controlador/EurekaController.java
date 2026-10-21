package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.service.EurekaService;
import java.math.BigDecimal;
import java.util.List;

public final class EurekaController {
    private final EurekaService servicio;

    public EurekaController(EurekaService servicio) {
        this.servicio = servicio;
    }

    public List<Movimiento> consultarMovimientos(String cuenta) {
        return servicio.traerMovimientos(cuenta);
    }

    public boolean registrarDeposito(String cuenta, BigDecimal importe) {
        return servicio.regDeposito(cuenta, importe) == 1;
    }
}
