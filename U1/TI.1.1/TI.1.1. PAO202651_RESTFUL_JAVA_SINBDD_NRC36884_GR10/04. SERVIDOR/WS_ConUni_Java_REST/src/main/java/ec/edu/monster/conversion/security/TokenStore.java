package ec.edu.monster.conversion.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Almacen de tokens en memoria volatil (sin base de datos).
 * Emite, valida y revoca tokens con caducidad configurable.
 */
@Component
public class TokenStore {

    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();
    private final Duration vigencia;

    public TokenStore(@Value("${monster.token.vigencia-minutos:30}") long minutos) {
        this.vigencia = Duration.ofMinutes(minutos);
    }

    public TokenInfo emitir(String usuario) {
        String valor = UUID.randomUUID().toString().replace("-", "");
        Instant ahora = Instant.now();
        TokenInfo info = new TokenInfo(valor, usuario, ahora, ahora.plus(vigencia));
        tokens.put(valor, info);
        return info;
    }

    public Optional<TokenInfo> validar(String valor) {
        if (valor == null || valor.isBlank()) {
            return Optional.empty();
        }
        TokenInfo info = tokens.get(valor);
        if (info == null) {
            return Optional.empty();
        }
        if (!info.activo()) {
            tokens.remove(valor);
            return Optional.empty();
        }
        return Optional.of(info);
    }

    public void revocar(String valor) {
        if (valor != null) {
            tokens.remove(valor);
        }
    }
}
