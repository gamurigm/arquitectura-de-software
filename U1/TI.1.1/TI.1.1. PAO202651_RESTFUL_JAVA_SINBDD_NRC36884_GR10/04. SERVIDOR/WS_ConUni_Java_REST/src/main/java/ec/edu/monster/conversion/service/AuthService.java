package ec.edu.monster.conversion.service;

import ec.edu.monster.conversion.dto.TokenResponse;
import ec.edu.monster.conversion.exception.TokenInvalidoException;
import ec.edu.monster.conversion.security.TokenInfo;
import ec.edu.monster.conversion.security.TokenStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Logica de autenticacion: valida credenciales en memoria y emite/revoca tokens.
 */
@Service
public class AuthService {

    private final TokenStore tokenStore;
    private final String usuarioValido;
    private final String claveValida;

    public AuthService(TokenStore tokenStore,
                       @Value("${monster.auth.usuario:monster}") String usuarioValido,
                       @Value("${monster.auth.clave:monster9}") String claveValida) {
        this.tokenStore = tokenStore;
        this.usuarioValido = usuarioValido;
        this.claveValida = claveValida;
    }

    public TokenResponse login(String usuario, String clave) {
        if (usuario == null || clave == null
                || !usuarioValido.equals(usuario.trim())
                || !claveValida.equals(clave)) {
            throw new TokenInvalidoException("Credenciales incorrectas. Verifique usuario y clave.");
        }
        TokenInfo info = tokenStore.emitir(usuario.trim());
        return new TokenResponse(true, info.valor(), "Bearer", info.usuario(),
                info.segundosRestantes(),
                "Autenticacion exitosa. Use el token en el encabezado Authorization: Bearer <token>.");
    }

    public void logout(String token) {
        tokenStore.revocar(token);
    }

    public TokenInfo validar(String token) {
        return tokenStore.validar(token)
                .orElseThrow(() -> new TokenInvalidoException("Token ausente, invalido o caducado."));
    }
}
