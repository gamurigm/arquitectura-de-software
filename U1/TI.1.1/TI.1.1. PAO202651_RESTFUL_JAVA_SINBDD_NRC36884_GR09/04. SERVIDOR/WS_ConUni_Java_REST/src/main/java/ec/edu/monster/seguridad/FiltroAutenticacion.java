package ec.edu.monster.seguridad;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.monster.modelo.dto.RespuestaError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Filtro de autorización por token.
 * Exige el encabezado "Authorization: Bearer &lt;token&gt;" en los recursos protegidos
 * y responde 401 con un JSON estandarizado cuando el token está ausente o es inválido.
 */
@Component
@Order(1)
public class FiltroAutenticacion extends OncePerRequestFilter {

    private static final Set<String> PREFIJOS_PUBLICOS = Set.of(
            "/api/auth/login",
            "/api/catalogo",
            "/api/salud",
            "/error",
            "/swagger-ui",
            "/v3/api-docs"
    );

    private final AlmacenTokens almacenTokens;
    private final ObjectMapper serializadorJson;

    public FiltroAutenticacion(AlmacenTokens almacenTokens, ObjectMapper serializadorJson) {
        this.almacenTokens = almacenTokens;
        this.serializadorJson = serializadorJson;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest solicitud, @NonNull HttpServletResponse respuesta, @NonNull FilterChain cadenaFiltros)
            throws ServletException, IOException {

        String ruta = solicitud.getRequestURI().substring(solicitud.getContextPath().length());

        if (esPublico(ruta) || "OPTIONS".equalsIgnoreCase(solicitud.getMethod())) {
            cadenaFiltros.doFilter(solicitud, respuesta);
            return;
        }

        String token = extraerToken(solicitud);
        if (almacenTokens.validar(token).isEmpty()) {
            escribirNoAutorizado(respuesta, ruta);
            return;
        }

        cadenaFiltros.doFilter(solicitud, respuesta);
    }

    private boolean esPublico(String ruta) {
        return PREFIJOS_PUBLICOS.stream().anyMatch(ruta::startsWith)
                || ruta.equals("/")
                || ruta.endsWith(".html")
                || ruta.endsWith(".js")
                || ruta.endsWith(".css")
                || ruta.endsWith(".png")
                || ruta.endsWith(".jpg")
                || ruta.endsWith(".ico");
    }

    private String extraerToken(HttpServletRequest solicitud) {
        String encabezado = solicitud.getHeader("Authorization");
        if (encabezado != null && encabezado.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return encabezado.substring(7).trim();
        }
        String alterno = solicitud.getHeader("X-Auth-Token");
        if (alterno != null && !alterno.isBlank()) {
            return alterno.trim();
        }
        return null;
    }

    private void escribirNoAutorizado(HttpServletResponse respuesta, String ruta) throws IOException {
        respuesta.setStatus(HttpStatus.UNAUTHORIZED.value());
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.setCharacterEncoding("UTF-8");
        RespuestaError error = new RespuestaError(false, HttpStatus.UNAUTHORIZED.value(),
                "No autorizado",
                "Token ausente, inválido o caducado. Inicie sesión nuevamente.",
                ruta, OffsetDateTime.now());
        respuesta.getWriter().write(serializadorJson.writeValueAsString(error));
    }
}
