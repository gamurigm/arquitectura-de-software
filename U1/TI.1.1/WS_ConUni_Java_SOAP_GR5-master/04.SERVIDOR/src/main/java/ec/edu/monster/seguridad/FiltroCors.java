package ec.edu.monster.seguridad;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtro CORS global para exponer servicios REST/SOAP a clientes web.
 */
public class FiltroCors implements Filter {

    private static final String PARAM_ORIGENES = "cors.allowed.origins";
    private static final String PARAM_METODOS = "cors.allowed.methods";
    private static final String PARAM_HEADERS = "cors.allowed.headers";
    private static final String PARAM_MAX_AGE = "cors.max.age";
    private static final String PARAM_CREDENCIALES = "cors.allow.credentials";

    private static final String ORIGENES_DEFECTO = "*";
    private static final String METODOS_DEFECTO = "GET,POST,PUT,DELETE,OPTIONS";
    private static final String HEADERS_DEFECTO = "Origin,Content-Type,Accept,Authorization,SOAPAction,X-Requested-With";
    private static final String MAX_AGE_DEFECTO = "3600";

    private Set<String> origenesPermitidos = Collections.emptySet();
    private boolean permitirTodosLosOrigenes;
    private boolean permitirCredenciales;
    private String metodosPermitidos;
    private String headersPermitidos;
    private String maxAge;

    @Override
    public void init(FilterConfig filterConfig) {
        origenesPermitidos = parseLista(filterConfig.getInitParameter(PARAM_ORIGENES), ORIGENES_DEFECTO);
        permitirTodosLosOrigenes = origenesPermitidos.contains("*");
        permitirCredenciales = Boolean.parseBoolean(
                valorPorDefecto(filterConfig.getInitParameter(PARAM_CREDENCIALES), "false")
        );
        metodosPermitidos = valorPorDefecto(filterConfig.getInitParameter(PARAM_METODOS), METODOS_DEFECTO);
        headersPermitidos = valorPorDefecto(filterConfig.getInitParameter(PARAM_HEADERS), HEADERS_DEFECTO);
        maxAge = valorPorDefecto(filterConfig.getInitParameter(PARAM_MAX_AGE), MAX_AGE_DEFECTO);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpRequest) || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        String origen = httpRequest.getHeader("Origin");

        if (origen != null && (permitirTodosLosOrigenes || origenesPermitidos.contains(origen))) {
            httpResponse.setHeader("Access-Control-Allow-Origin", permitirTodosLosOrigenes ? "*" : origen);
            httpResponse.setHeader("Vary", "Origin");
        }

        if (permitirCredenciales && !permitirTodosLosOrigenes) {
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        }

        httpResponse.setHeader("Access-Control-Allow-Methods", metodosPermitidos);
        httpResponse.setHeader("Access-Control-Allow-Headers", headersPermitidos);
        httpResponse.setHeader("Access-Control-Max-Age", maxAge);

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No resources to release.
    }

    private static Set<String> parseLista(String valor, String valorDefecto) {
        return Arrays.stream(valorPorDefecto(valor, valorDefecto).split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static String valorPorDefecto(String valor, String valorDefecto) {
        if (valor == null || valor.isBlank()) {
            return valorDefecto;
        }
        return valor;
    }
}