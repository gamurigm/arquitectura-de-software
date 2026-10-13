package ec.edu.monster.climov.controlador;

import ec.edu.monster.climov.modelo.CatalogoResponse;
import ec.edu.monster.climov.modelo.ConversionRequest;
import ec.edu.monster.climov.modelo.ConversionResponse;
import ec.edu.monster.climov.modelo.LoginRequest;
import ec.edu.monster.climov.modelo.TokenResponse;
import ec.edu.monster.climov.servicios.ApiClient;
import ec.edu.monster.climov.servicios.ApiService;
import retrofit2.Call;

public class ControladorCliente {
    private final ApiService servicio;

    public ControladorCliente(String baseUrl) {
        servicio = ApiClient.crear(baseUrl);
    }

    public Call<TokenResponse> login(LoginRequest solicitud) {
        return servicio.login(solicitud);
    }

    public Call<CatalogoResponse> catalogo() {
        return servicio.catalogo();
    }

    public Call<ConversionResponse> convertir(String magnitud, String bearer, ConversionRequest solicitud) {
        return switch (magnitud) {
            case "masa" -> servicio.convertirMasa(bearer, solicitud);
            case "temperatura" -> servicio.convertirTemperatura(bearer, solicitud);
            default -> servicio.convertirLongitud(bearer, solicitud);
        };
    }
}
