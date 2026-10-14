package ec.edu.monster.controlador;

import android.content.Context;
import ec.edu.monster.servicios.ServicioSOAP;
import java.io.IOException;

public final class ControladorConversion {

    private final ServicioSOAP servicio;

    public ControladorConversion(Context contexto, String token) {
        servicio = new ServicioSOAP(contexto, token);
    }

    public double convertir(String tipo, double valor, String unidadOrigen, String unidadDestino)
            throws IOException {
        if (!Double.isFinite(valor)) {
            throw new IllegalArgumentException("Ingrese un valor válido.");
        }
        return servicio.convertir(tipo, valor, unidadOrigen, unidadDestino);
    }
}
