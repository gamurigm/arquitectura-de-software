package ec.edu.monster.servicios;

import java.util.concurrent.TimeUnit;
import java.net.URI;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Fabrica de instancias de {@link ServicioApi}. */
public final class ClienteApi {

    private ClienteApi() {
    }

    public static ServicioApi crear(String urlBase) {
        urlBase = validarUrlBase(urlBase);
        OkHttpClient clienteHttp = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        String url = urlBase.endsWith("/") ? urlBase : urlBase + "/";

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(clienteHttp)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ServicioApi.class);
    }

    public static String validarUrlBase(String urlBase) {
        try {
            URI uri = URI.create(urlBase == null ? "" : urlBase.trim());
            if (!uri.isAbsolute() || uri.getHost() == null || uri.getUserInfo() != null
                    || uri.getQuery() != null || uri.getFragment() != null
                    || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
                throw new IllegalArgumentException();
            }
            return uri.toString();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Ingrese una URL HTTP o HTTPS válida, por ejemplo http://10.0.2.2:8080.", ex);
        }
    }
}
