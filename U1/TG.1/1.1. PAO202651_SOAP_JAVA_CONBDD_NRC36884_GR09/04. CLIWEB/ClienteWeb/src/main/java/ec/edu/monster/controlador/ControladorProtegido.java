package ec.edu.monster.controlador;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

abstract class ControladorProtegido extends HttpServlet {
    private static final SecureRandom ALEATORIO = new SecureRandom();

    boolean exigirSesion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Content-Type-Options", "nosniff");
        var sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuario") == null || sesion.getAttribute("token") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        request.setAttribute("csrf", tokenCsrf(request));
        return true;
    }

    String tokenCsrf(HttpServletRequest request) {
        var sesion = request.getSession(true);
        String token = (String) sesion.getAttribute("csrf");
        if (token == null) {
            byte[] bytes = new byte[32];
            ALEATORIO.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            sesion.setAttribute("csrf", token);
        }
        return token;
    }

    boolean validarCsrf(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var sesion = request.getSession(false);
        String esperado = sesion == null ? null : (String) sesion.getAttribute("csrf");
        String recibido = request.getParameter("_csrf");
        if (esperado == null || recibido == null || !MessageDigest.isEqual(
                esperado.getBytes(StandardCharsets.US_ASCII), recibido.getBytes(StandardCharsets.US_ASCII))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
