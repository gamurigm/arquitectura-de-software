package ec.edu.monster.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/menu")
public final class MenuServlet extends ControladorProtegido {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (exigirSesion(request, response)) {
            request.getRequestDispatcher("/WEB-INF/jsp/menu.jsp").forward(request, response);
        }
    }
}
