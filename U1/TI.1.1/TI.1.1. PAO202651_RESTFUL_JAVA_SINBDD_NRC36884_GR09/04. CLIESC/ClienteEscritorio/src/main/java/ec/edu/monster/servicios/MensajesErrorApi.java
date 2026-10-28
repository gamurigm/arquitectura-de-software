package ec.edu.monster.servicios;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;

/**
 * Convierte fallos HTTP y de conexión en mensajes claros para la interfaz.
 */
public final class MensajesErrorApi {

    private MensajesErrorApi() {
    }

    public static String paraInicioSesion(Throwable error) {
        ExcepcionApi excepcionApi = buscarExcepcionApi(error);
        if (excepcionApi != null && excepcionApi.esNoAutorizado()) {
            return "Credenciales incorrectas. Verifique usuario y clave (HTTP 401).";
        }
        return paraOperacion(error);
    }

    public static String paraOperacion(Throwable error) {
        ExcepcionApi excepcionApi = buscarExcepcionApi(error);
        if (excepcionApi != null) {
            return excepcionApi.mensajeParaUsuario();
        }
        if (contiene(error, UnknownHostException.class)) {
            return "No se encontró el servidor. Revise la URL y la conexión de red.";
        }
        if (contiene(error, HttpConnectTimeoutException.class) || contiene(error, ConnectException.class)) {
            return "No se pudo conectar con el servidor. Verifique que esté en línea y revise la URL.";
        }
        if (contiene(error, HttpTimeoutException.class)) {
            return "El servidor tardó demasiado en responder. Intente nuevamente.";
        }
        if (contiene(error, IOException.class)) {
            return "No se pudo completar la comunicación con el servidor. Revise la red e intente nuevamente.";
        }
        String mensaje = error == null ? null : error.getMessage();
        return mensaje == null || mensaje.isBlank()
                ? "Ocurrió un error inesperado al comunicarse con el servidor."
                : mensaje;
    }

    private static ExcepcionApi buscarExcepcionApi(Throwable error) {
        for (Throwable actual = error; actual != null; actual = actual.getCause()) {
            if (actual instanceof ExcepcionApi excepcionApi) {
                return excepcionApi;
            }
        }
        return null;
    }

    private static boolean contiene(Throwable error, Class<? extends Throwable> tipo) {
        for (Throwable actual = error; actual != null; actual = actual.getCause()) {
            if (tipo.isInstance(actual)) {
                return true;
            }
        }
        return false;
    }
}
