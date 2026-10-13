package ec.edu.monster.climov.servicios;

import ec.edu.monster.climov.modelo.CatalogoResponse;
import ec.edu.monster.climov.modelo.ConversionRequest;
import ec.edu.monster.climov.modelo.ConversionResponse;
import ec.edu.monster.climov.modelo.LoginRequest;
import ec.edu.monster.climov.modelo.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/** Definicion de los endpoints REST consumidos por el cliente movil. */
public interface ApiService {

    @POST("api/auth/login")
    Call<TokenResponse> login(@Body LoginRequest request);

    @POST("api/conversiones/longitud")
    Call<ConversionResponse> convertirLongitud(@Header("Authorization") String bearer,
                                               @Body ConversionRequest request);

    @POST("api/conversiones/masa")
    Call<ConversionResponse> convertirMasa(@Header("Authorization") String bearer,
                                           @Body ConversionRequest request);

    @POST("api/conversiones/temperatura")
    Call<ConversionResponse> convertirTemperatura(@Header("Authorization") String bearer,
                                                  @Body ConversionRequest request);

    @GET("api/catalogo")
    Call<CatalogoResponse> catalogo();
}
