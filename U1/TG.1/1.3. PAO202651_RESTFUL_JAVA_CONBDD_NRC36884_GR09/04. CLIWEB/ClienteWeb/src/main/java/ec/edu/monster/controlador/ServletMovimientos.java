package ec.edu.monster.controlador;

import ec.edu.monster.servicios.ClienteEurekaRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/movimientos")
public class ServletMovimientos extends HttpServlet {
    private final ClienteEurekaRest cliente = new ClienteEurekaRest();

    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String cuenta = request.getParameter("cuenta");
        request.setAttribute("usuario", request.getSession(false).getAttribute("usuario"));
        request.setAttribute("cuenta", cuenta == null ? "" : cuenta);
        try {
            if (cuenta == null || !cuenta.matches("[0-9]{8}")) request.setAttribute("mensaje", "Ingrese una cuenta válida de ocho dígitos.");
            else request.setAttribute("movimientos", cliente.traerMovimientos(cuenta));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            request.setAttribute("mensaje", "La solicitud fue interrumpida.");
            request.setAttribute("error", true);
        } catch (Exception e) {
            request.setAttribute("mensaje", "No se pudieron consultar los movimientos.");
            request.setAttribute("error", true);
        }
        request.getRequestDispatcher("/WEB-INF/vista/inicio.jsp").forward(request, response);
    }
}
