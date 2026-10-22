package ec.edu.monster.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/inicio")
public class ServletInicio extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        jakarta.servlet.http.HttpSession sesion = request.getSession(false);
        if (sesion.getAttribute("csrfToken") == null) {
            byte[] bytes = new byte[32];
            new java.security.SecureRandom().nextBytes(bytes);
            sesion.setAttribute("csrfToken", java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes));
        }
        request.setAttribute("usuario", sesion.getAttribute("usuario"));
        request.setAttribute("csrfToken", sesion.getAttribute("csrfToken"));
        request.setAttribute("claveOperacion", java.util.UUID.randomUUID().toString());
        request.getRequestDispatcher("/WEB-INF/vista/inicio.jsp").forward(request, response);
    }
}
