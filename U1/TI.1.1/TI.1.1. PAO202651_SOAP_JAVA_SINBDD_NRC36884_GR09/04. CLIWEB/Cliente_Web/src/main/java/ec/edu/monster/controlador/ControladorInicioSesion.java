package ec.edu.monster.controlador;

import ec.edu.monster.servicios.ClienteSOAP;
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
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        solicitud.setCharacterEncoding("UTF-8");
        String usuario = solicitud.getParameter("txtUsuario");
        String contrasenia = solicitud.getParameter("txtClave");
        if (usuario == null || usuario.isBlank() || contrasenia == null || contrasenia.isBlank()) {
            solicitud.setAttribute("error", "Ingrese usuario y contraseña.");
            solicitud.getRequestDispatcher("inicio.jsp").forward(solicitud, respuesta);
            return;
        }

        try {
            String token = new ClienteSOAP(null).iniciarSesion(usuario.trim(), contrasenia);
            HttpSession session = solicitud.getSession(true);
            session.setMaxInactiveInterval(15 * 60);
            session.setAttribute("miToken", token);
            respuesta.sendRedirect("conversor.jsp");
        } catch (RuntimeException ex) {
            solicitud.setAttribute("error", mensajeUsuario(ex));
            solicitud.getRequestDispatcher("inicio.jsp").forward(solicitud, respuesta);
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
