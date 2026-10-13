package ec.edu.monster.climov.servicios;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Fabrica de instancias de {@link ApiService}. */
public final class ApiClient {

    private ApiClient() {
    }

    public static ApiService crear(String baseUrl) {
        OkHttpClient http = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        String url = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(http)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }
}
