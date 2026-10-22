package ec.edu.monster.seguridad;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SesionesActivas {
    private static final SesionesActivas INSTANCIA = new SesionesActivas();
    private static final long DURACION_SEGUNDOS = 1200;
    private static final SecureRandom ALEATORIO = new SecureRandom();
    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();

    private SesionesActivas() { }
    public static SesionesActivas getInstancia() { return INSTANCIA; }

    public String crear(String usuario, String codigoCliente) {
        byte[] bytes = new byte[32];
        ALEATORIO.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sesiones.put(token, new Sesion(usuario, codigoCliente, Instant.now().plusSeconds(DURACION_SEGUNDOS)));
        return token;
    }

    public Sesion obtener(String token) {
        if (token == null || token.isBlank()) return null;
        Sesion sesion = sesiones.get(token);
        if (sesion == null) return null;
        if (!sesion.expira().isAfter(Instant.now())) {
            sesiones.remove(token, sesion);
            return null;
        }
        return sesion;
    }

    public void revocar(String token) {
        if (token != null) sesiones.remove(token);
    }

    public record Sesion(String usuario, String codigoCliente, Instant expira) { }
}
