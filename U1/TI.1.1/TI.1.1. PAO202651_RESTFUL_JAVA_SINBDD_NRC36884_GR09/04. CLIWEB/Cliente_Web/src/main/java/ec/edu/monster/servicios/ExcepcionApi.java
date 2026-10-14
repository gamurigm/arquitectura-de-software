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
}
