package ec.edu.monster.servicios;

import ec.edu.monster.modelo.RespuestaCatalogo;
import ec.edu.monster.modelo.SolicitudConversion;
import ec.edu.monster.modelo.RespuestaConversion;
import ec.edu.monster.modelo.SolicitudInicioSesion;
import ec.edu.monster.modelo.RespuestaToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/** Definicion de los endpoints REST consumidos por el cliente movil. */
public interface ServicioApi {

    @POST("api/auth/login")
    Call<RespuestaToken> iniciarSesion(@Body SolicitudInicioSesion solicitud);

    @POST("api/conversiones/longitud")
    Call<RespuestaConversion> convertirLongitud(@Header("Authorization") String bearer,
                                               @Body SolicitudConversion solicitud);

    @POST("api/conversiones/masa")
    Call<RespuestaConversion> convertirMasa(@Header("Authorization") String bearer,
                                           @Body SolicitudConversion solicitud);

    @POST("api/conversiones/temperatura")
    Call<RespuestaConversion> convertirTemperatura(@Header("Authorization") String bearer,
                                                  @Body SolicitudConversion solicitud);

    @GET("api/catalogo")
    Call<RespuestaCatalogo> obtenerCatalogo();
}
