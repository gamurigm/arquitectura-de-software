package ec.edu.monster.controlador;

import ec.edu.monster.servicios.ClienteEurekaRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class ServletInicioSesion extends HttpServlet {
    private final ClienteEurekaRest cliente = new ClienteEurekaRest();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) != null && request.getSession(false).getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/inicio");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/vista/login.jsp").forward(request, response);
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");
        try {
            String token = cliente.iniciarSesion(usuario, contrasena);
            if (token != null && !token.isBlank()) {
                HttpSession sesion = request.getSession(true);
                request.changeSessionId();
                sesion.setAttribute("usuario", usuario.strip());
                sesion.setAttribute("token", token);
                sesion.setMaxInactiveInterval(30 * 60);
                response.sendRedirect(request.getContextPath() + "/inicio");
                return;
            }
            request.setAttribute("error", "Credenciales incorrectas.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            request.setAttribute("error", "La solicitud fue interrumpida.");
        } catch (Exception e) {
            request.setAttribute("error", "No se pudo comunicar con EurekaBank.");
        }
        doGet(request, response);
    }
}
