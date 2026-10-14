package ec.edu.monster.excepciones;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando la unidad solicitada no existe en el catalogo de la magnitud.
 */
public class ExcepcionUnidadInvalida extends ExcepcionApi {

    public ExcepcionUnidadInvalida(String mensaje) {
        super(HttpStatus.BAD_REQUEST, "Unidad invalida", mensaje);
    }
}
