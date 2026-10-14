package ec.edu.monster.excepciones;

import org.springframework.http.HttpStatus;

/**
 * Excepcion base de la API. Transporta el estado HTTP y el titulo del error.
 */
public class ExcepcionApi extends RuntimeException {

    private final HttpStatus codigoHttp;
    private final String error;

    public ExcepcionApi(HttpStatus codigoHttp, String error, String mensaje) {
        super(mensaje);
        this.codigoHttp = codigoHttp;
        this.error = error;
    }

    public HttpStatus obtenerEstadoHttp() {
        return codigoHttp;
    }

    public String obtenerError() {
        return error;
    }
}
