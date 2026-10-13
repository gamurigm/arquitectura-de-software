package ec.edu.monster.conversion.exception;

import org.springframework.http.HttpStatus;

/**
 * Se lanza cuando las credenciales o el token no son validos.
 */
public class TokenInvalidoException extends ApiException {

    public TokenInvalidoException(String mensaje) {
        super(HttpStatus.UNAUTHORIZED, "No autorizado", mensaje);
    }
}
