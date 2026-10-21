package ec.edu.monster.seguridad;

import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

public class AdministradorTokens {
    
    private static final Set<String> tokensActivos = new HashSet<>();

    public static String generarToken() {
    
        String token = UUID.randomUUID().toString();
        tokensActivos.add(token);
        return token;
    }

    public static boolean validarToken(String token) {
        return tokensActivos.contains(token);
    }
}