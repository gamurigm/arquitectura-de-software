package ec.edu.monster.controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/cerrar-sesion", "/CerrarSesionServlet"})
public class ControladorCerrarSesion extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException {
        HttpSession session = solicitud.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        respuesta.sendRedirect("inicio.jsp");
    }
}
