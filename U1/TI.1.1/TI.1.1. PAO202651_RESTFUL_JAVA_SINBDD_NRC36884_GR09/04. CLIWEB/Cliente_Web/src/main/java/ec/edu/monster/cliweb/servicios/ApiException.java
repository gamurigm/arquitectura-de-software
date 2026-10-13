package ec.edu.monster.cliweb.servicios;

/**
 * Excepcion de la API REST con el codigo de estado HTTP.
 */
public class ApiException extends Exception {

    private final int status;

    public ApiException(int status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public boolean esNoAutorizado() {
        return status == 401;
    }
}
