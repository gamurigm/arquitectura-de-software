package ec.edu.monster.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import ec.edu.monster.service.EurekaService;

@WebServlet("/salir")
public final class CerrarSesionServlet extends ControladorProtegido {
    private final EurekaService servicio = new EurekaService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!exigirSesion(request, response) || !validarCsrf(request, response)) return;
        var session = request.getSession(false);
        if (session != null) {
            try { servicio.cerrarSesion((String) session.getAttribute("token")); }
            catch (RuntimeException | IOException ignored) { }
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
