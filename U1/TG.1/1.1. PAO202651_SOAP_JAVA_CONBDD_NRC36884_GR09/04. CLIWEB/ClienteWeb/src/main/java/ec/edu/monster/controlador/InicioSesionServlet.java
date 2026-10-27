package ec.edu.monster.controlador;

import ec.edu.monster.service.EurekaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public final class InicioSesionServlet extends ControladorProtegido {
    private final EurekaService servicio = new EurekaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("csrf", tokenCsrf(request));
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!validarCsrf(request, response)) return;
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");
        String token = null;
        try {
            if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.isEmpty()) {
                request.setAttribute("error", "Ingrese usuario y contraseña.");
                doGet(request, response);
                return;
            }
            token = servicio.crearSesion(usuario.strip(), contrasena);
            if (token == null || token.isBlank()) {
                request.setAttribute("error", "Credenciales incorrectas.");
                doGet(request, response);
                return;
            }
            var cuentas = servicio.traerCuentas(token);
            var sesion = request.getSession(true);
            request.changeSessionId();
            sesion.setAttribute("usuario", usuario.strip());
            sesion.setAttribute("token", token);
            sesion.setAttribute("cuentas", cuentas);
            response.sendRedirect(request.getContextPath() + "/menu");
        } catch (RuntimeException | IOException e) {
            if (token != null && !token.isBlank()) {
                try { servicio.cerrarSesion(token); } catch (IOException cierre) { e.addSuppressed(cierre); }
            }
            request.setAttribute("error", "No se pudo conectar con EurekaBank. Intente más tarde.");
            doGet(request, response);
        }
    }
}
