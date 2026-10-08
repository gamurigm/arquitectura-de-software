package ec.edu.monster.seguridad;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AdministradorTokens {

    private static final Set<String> TOKENS = ConcurrentHashMap.newKeySet();

    private AdministradorTokens() {
    }

    public static String generarToken() {
        String token = UUID.randomUUID().toString();
        TOKENS.add(token);
        return token;
    }

    public static boolean validarToken(String token) {
        return token != null && TOKENS.contains(token);
    }
}