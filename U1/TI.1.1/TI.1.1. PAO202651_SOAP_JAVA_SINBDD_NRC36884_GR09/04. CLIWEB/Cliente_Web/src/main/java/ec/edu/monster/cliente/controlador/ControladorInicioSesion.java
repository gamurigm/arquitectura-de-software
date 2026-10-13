package ec.edu.monster.cliente.controlador;

import ec.edu.monster.cliente.servicios.ClienteSOAP;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/iniciar-sesion", "/LoginServlet"})
public class ControladorInicioSesion extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String usuario = request.getParameter("txtUsuario");
        String contrasenia = request.getParameter("txtClave");
        if (usuario == null || usuario.isBlank() || contrasenia == null || contrasenia.isBlank()) {
            request.setAttribute("error", "Ingrese usuario y contraseña.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        try {
            String token = new ClienteSOAP(null).login(usuario.trim(), contrasenia);
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(15 * 60);
            session.setAttribute("miToken", token);
            response.sendRedirect("conversor.jsp");
        } catch (RuntimeException ex) {
            request.setAttribute("error", mensajeUsuario(ex));
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    private static String mensajeUsuario(Exception ex) {
        for (Throwable causa = ex; causa != null; causa = causa.getCause()) {
            String mensaje = causa.getMessage();
            if (mensaje != null && mensaje.toLowerCase().contains("credenciales incorrectas")) {
                return "Usuario o contraseña incorrectos.";
            }
        }
        return "No se pudo iniciar sesión con el servidor SOAP. Verifique que esté activo.";
    }
}
