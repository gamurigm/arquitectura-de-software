package ec.edu.monster.conversion.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando la unidad solicitada no existe en el catalogo de la magnitud.
 */
public class UnidadInvalidaException extends ApiException {

    public UnidadInvalidaException(String mensaje) {
        super(HttpStatus.BAD_REQUEST, "Unidad invalida", mensaje);
    }
}
