package ec.edu.monster.seguridad;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AdministradorTokens {

    private static final Duration VIGENCIA = Duration.ofMinutes(10
    );
    private static final ConcurrentHashMap<String, Instant> TOKENS = new ConcurrentHashMap<>();

    private AdministradorTokens() {
    }

    public static String generarToken() {
        Instant ahora = Instant.now();
        TOKENS.entrySet().removeIf(entrada -> !entrada.getValue().isAfter(ahora));
        String token = UUID.randomUUID().toString();
        TOKENS.put(token, ahora.plus(VIGENCIA));
        return token;
    }

    public static boolean validarToken(String token) {
        return validarToken(token, Instant.now());
    }

    static boolean validarToken(String token, Instant ahora) {
        if (token == null) {
            return false;
        }
        Instant expiracion = TOKENS.get(token);
        if (expiracion == null) {
            return false;
        }
        if (!expiracion.isAfter(ahora)) {
            TOKENS.remove(token, expiracion);
            return false;
        }
        return true;
    }
}
