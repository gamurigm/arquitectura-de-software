package ec.edu.monster.conversion.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.monster.conversion.dto.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Set;

/**
 * Filtro de autorizacion por token.
 * Exige el encabezado "Authorization: Bearer &lt;token&gt;" en los recursos protegidos
 * y responde 401 con un JSON estandarizado cuando el token es ausente o invalido.
 */
@Component
@Order(1)
public class AuthFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/api/auth/login",
            "/api/catalogo",
            "/api/salud",
            "/error",
            "/swagger-ui",
            "/v3/api-docs"
    );

    private final TokenStore tokenStore;
    private final ObjectMapper objectMapper;

    public AuthFilter(TokenStore tokenStore, ObjectMapper objectMapper) {
        this.tokenStore = tokenStore;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (esPublico(path) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String token = extraerToken(request);
        if (tokenStore.validar(token).isEmpty()) {
            escribirNoAutorizado(response, path);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean esPublico(String path) {
        return PUBLIC_PREFIXES.stream().anyMatch(path::startsWith)
                || path.equals("/")
                || path.endsWith(".html")
                || path.endsWith(".js")
                || path.endsWith(".css")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".ico");
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return header.substring(7).trim();
        }
        String alterno = request.getHeader("X-Auth-Token");
        if (alterno != null && !alterno.isBlank()) {
            return alterno.trim();
        }
        return null;
    }

    private void escribirNoAutorizado(HttpServletResponse response, String path) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ErrorResponse error = new ErrorResponse(false, HttpStatus.UNAUTHORIZED.value(),
                "No autorizado",
                "Token ausente, invalido o caducado. Inicie sesion nuevamente.",
                path, OffsetDateTime.now());
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
