package ec.edu.monster.excepciones;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando las credenciales o el token no son válidos.
 */
public class ExcepcionTokenInvalido extends ExcepcionApi {

    public ExcepcionTokenInvalido(String mensaje) {
        super(HttpStatus.UNAUTHORIZED, "No autorizado", mensaje);
    }
}
