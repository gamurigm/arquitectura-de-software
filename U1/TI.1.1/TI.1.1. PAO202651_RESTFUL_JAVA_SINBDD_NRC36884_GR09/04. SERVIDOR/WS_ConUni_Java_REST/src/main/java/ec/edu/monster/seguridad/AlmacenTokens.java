package ec.edu.monster.seguridad;

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
public class AlmacenTokens {

    private final Map<String, InformacionToken> tokens = new ConcurrentHashMap<>();
    private final Duration vigencia;

    public AlmacenTokens(@Value("${monster.token.vigencia-minutos:10}") long minutos) {
        this.vigencia = Duration.ofMinutes(minutos);
    }

    public InformacionToken emitir(String usuario) {
        String valor = UUID.randomUUID().toString().replace("-", "");
        Instant ahora = Instant.now();
        InformacionToken info = new InformacionToken(valor, usuario, ahora, ahora.plus(vigencia));
        tokens.put(valor, info);
        return info;
    }

    public Optional<InformacionToken> validar(String valor) {
        if (valor == null || valor.isBlank()) {
            return Optional.empty();
        }
        InformacionToken info = tokens.get(valor);
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
