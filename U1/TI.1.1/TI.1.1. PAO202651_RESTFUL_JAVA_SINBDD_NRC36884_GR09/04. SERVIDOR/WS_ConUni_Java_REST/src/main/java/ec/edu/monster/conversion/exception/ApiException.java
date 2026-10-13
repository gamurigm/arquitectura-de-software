package ec.edu.monster.conversion.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepcion base de la API. Transporta el estado HTTP y el titulo del error.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String error;

    public ApiException(HttpStatus status, String error, String mensaje) {
        super(mensaje);
        this.status = status;
        this.error = error;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
