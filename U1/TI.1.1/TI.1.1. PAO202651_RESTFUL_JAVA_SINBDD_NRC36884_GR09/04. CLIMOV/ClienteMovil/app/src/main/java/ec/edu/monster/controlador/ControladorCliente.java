package ec.edu.monster.controlador;

import ec.edu.monster.modelo.RespuestaCatalogo;
import ec.edu.monster.modelo.SolicitudConversion;
import ec.edu.monster.modelo.RespuestaConversion;
import ec.edu.monster.modelo.SolicitudInicioSesion;
import ec.edu.monster.modelo.RespuestaToken;
import ec.edu.monster.servicios.ClienteApi;
import ec.edu.monster.servicios.ServicioApi;
import retrofit2.Call;

public class ControladorCliente {
    private final ServicioApi servicio;

    public ControladorCliente(String urlBase) {
        servicio = ClienteApi.crear(urlBase);
    }

    public Call<RespuestaToken> iniciarSesion(SolicitudInicioSesion solicitud) {
        return servicio.iniciarSesion(solicitud);
    }

    public Call<RespuestaCatalogo> obtenerCatalogo() {
        return servicio.obtenerCatalogo();
    }

    public Call<RespuestaConversion> convertir(String magnitud, String bearer, SolicitudConversion solicitud) {
        return switch (magnitud) {
            case "masa" -> servicio.convertirMasa(bearer, solicitud);
            case "temperatura" -> servicio.convertirTemperatura(bearer, solicitud);
            default -> servicio.convertirLongitud(bearer, solicitud);
        };
    }
}
