package ec.edu.monster.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class ServletCerrarSesion extends HttpServlet {
    private final ec.edu.monster.servicios.ClienteEurekaRest cliente = new ec.edu.monster.servicios.ClienteEurekaRest();

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            Object esperado = sesion.getAttribute("csrfToken");
            String recibido = request.getParameter("csrfToken");
            if (esperado instanceof String csrf && recibido != null && java.security.MessageDigest.isEqual(
                    csrf.getBytes(java.nio.charset.StandardCharsets.UTF_8), recibido.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
                Object token = sesion.getAttribute("token");
                if (token instanceof String valor) {
                    try { cliente.cerrarSesion(valor); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    catch (Exception ignored) { }
                }
                sesion.invalidate();
            }
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
