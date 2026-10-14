package ec.edu.monster.servicios;

import ec.edu.monster.modelo.dto.RespuestaToken;
import ec.edu.monster.excepciones.ExcepcionTokenInvalido;
import ec.edu.monster.seguridad.InformacionToken;
import ec.edu.monster.seguridad.AlmacenTokens;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Logica de autenticacion: valida credenciales en memoria y emite/revoca tokens.
 */
@Service
public class ServicioAutenticacion {

    private final AlmacenTokens almacenTokens;
    private final String usuarioValido;
    private final String claveValida;

    public ServicioAutenticacion(AlmacenTokens almacenTokens,
                       @Value("${monster.auth.usuario:Monster}") String usuarioValido,
                       @Value("${monster.auth.clave:Monster9}") String claveValida) {
        this.almacenTokens = almacenTokens;
        this.usuarioValido = usuarioValido;
        this.claveValida = claveValida;
    }

    public RespuestaToken iniciarSesion(String usuario, String clave) {
        if (usuario == null || clave == null
                || !usuarioValido.equals(usuario.trim())
                || !claveValida.equals(clave)) {
            throw new ExcepcionTokenInvalido("Credenciales incorrectas. Verifique usuario y clave.");
        }
        InformacionToken info = almacenTokens.emitir(usuario.trim());
        return new RespuestaToken(true, info.valor(), "Bearer", info.usuario(),
                info.segundosRestantes(),
                "Autenticacion exitosa. Use el token en el encabezado Authorization: Bearer <token>.");
    }

    public void cerrarSesion(String token) {
        almacenTokens.revocar(token);
    }

    public InformacionToken validar(String token) {
        return almacenTokens.validar(token)
                .orElseThrow(() -> new ExcepcionTokenInvalido("Token ausente, invalido o caducado."));
    }
}
