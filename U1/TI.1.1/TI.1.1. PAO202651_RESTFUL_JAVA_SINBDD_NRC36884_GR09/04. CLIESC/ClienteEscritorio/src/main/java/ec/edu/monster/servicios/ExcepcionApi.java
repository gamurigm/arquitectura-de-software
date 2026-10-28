package ec.edu.monster.servicios;

/**
 * Excepcion de la API REST con el codigo de estado HTTP.
 */
public class ExcepcionApi extends Exception {

    private final int codigoHttp;

    public ExcepcionApi(int codigoHttp, String mensaje) {
        super(mensaje);
        this.codigoHttp = codigoHttp;
    }

    public int obtenerEstadoHttp() {
        return codigoHttp;
    }

    public boolean esNoAutorizado() {
        return codigoHttp == 401;
    }

    public boolean esErrorServidor() {
        return codigoHttp >= 500;
    }

    public String mensajeParaUsuario() {
        if (codigoHttp == 401) {
            return "El servidor rechazó la autorización (HTTP 401).";
        }
        if (codigoHttp == 403) {
            return "El servidor denegó el acceso a esta operación (HTTP 403).";
        }
        if (codigoHttp == 404) {
            return "No se encontró la ruta solicitada en el servidor (HTTP 404).";
        }
        if (codigoHttp == 429) {
            return "El servidor está recibiendo demasiadas solicitudes. Intente más tarde.";
        }
        if (esErrorServidor()) {
            return "El servidor presentó un error (HTTP " + codigoHttp + "). Intente más tarde.";
        }
        if (getMessage() != null && !getMessage().isBlank()) {
            return getMessage() + " (HTTP " + codigoHttp + ").";
        }
        return "El servidor rechazó la solicitud (HTTP " + codigoHttp + ").";
    }
}
