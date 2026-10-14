package ec.edu.monster.controlador;

import ec.edu.monster.servicios.ClienteApi;
import ec.edu.monster.servicios.ExcepcionApi;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet de autenticación. Obtiene el token REST y lo guarda en la sesión.
 */
@WebServlet(name = "ServletInicioSesion", urlPatterns = {"/login"})
public class ServletInicioSesion extends HttpServlet {

    private static final String BASE_POR_DEFECTO = "http://localhost:8080";

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        solicitud.getRequestDispatcher("/WEB-INF/vista/inicio_sesion.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String urlBase = valor(solicitud.getParameter("urlBase"), BASE_POR_DEFECTO);
        String usuario = solicitud.getParameter("usuario");
        String clave = solicitud.getParameter("clave");
        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank()) {
            solicitud.setAttribute("error", "Ingrese usuario y contraseña.");
            solicitud.setAttribute("urlBase", urlBase);
            solicitud.setAttribute("usuario", usuario);
            solicitud.getRequestDispatcher("/WEB-INF/vista/inicio_sesion.jsp").forward(solicitud, respuesta);
            return;
        }
        usuario = usuario.trim();

        try {
            ClienteApi api = new ClienteApi(urlBase);
            String token = api.iniciarSesion(usuario, clave);
            HttpSession sesion = solicitud.getSession(true);
            sesion.setAttribute("token", token);
            sesion.setAttribute("usuario", usuario);
            sesion.setAttribute("urlBase", urlBase);
            respuesta.sendRedirect(solicitud.getContextPath() + "/convertir");
        } catch (ExcepcionApi e) {
            solicitud.setAttribute("error", "No fue posible iniciar sesion: " + e.getMessage());
            solicitud.setAttribute("urlBase", urlBase);
            solicitud.getRequestDispatcher("/WEB-INF/vista/inicio_sesion.jsp").forward(solicitud, respuesta);
        } catch (Exception e) {
            solicitud.setAttribute("error", "Error de conexion con el servidor: " + e.getMessage());
            solicitud.setAttribute("urlBase", urlBase);
            solicitud.getRequestDispatcher("/WEB-INF/vista/inicio_sesion.jsp").forward(solicitud, respuesta);
        }
    }

    private static String valor(String valor, String porDefecto) {
        return valor == null || valor.isBlank() ? porDefecto : valor.trim();
    }
}
