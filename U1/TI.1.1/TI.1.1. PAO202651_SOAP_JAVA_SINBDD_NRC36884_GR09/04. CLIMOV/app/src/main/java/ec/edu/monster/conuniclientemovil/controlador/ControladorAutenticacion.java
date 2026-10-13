package ec.edu.monster.conuniclientemovil.controlador;

import android.content.Context;
import ec.edu.monster.conuniclientemovil.servicios.ServicioSOAP;
import java.io.IOException;

public final class ControladorAutenticacion {

    private final ServicioSOAP servicio;

    public ControladorAutenticacion(Context contexto) {
        servicio = new ServicioSOAP(contexto, null);
    }

    public String iniciarSesion(String usuario, String contrasenia) throws IOException {
        if (usuario == null || usuario.isBlank() || contrasenia == null || contrasenia.isBlank()) {
            throw new IllegalArgumentException("Ingrese usuario y contraseña.");
        }
        return servicio.iniciarSesion(usuario.trim(), contrasenia);
    }
}
