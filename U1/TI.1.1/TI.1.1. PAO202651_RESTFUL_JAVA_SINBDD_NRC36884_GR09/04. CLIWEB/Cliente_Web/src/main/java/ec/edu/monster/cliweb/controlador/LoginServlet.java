package ec.edu.monster.cliweb.controlador;

import ec.edu.monster.cliweb.servicios.ApiClient;
import ec.edu.monster.cliweb.servicios.ApiException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet de autenticacion. Obtiene el token del servidor REST y lo guarda en la sesion.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private static final String BASE_POR_DEFECTO = "http://localhost:8080";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/vista/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String baseUrl = valor(request.getParameter("baseUrl"), BASE_POR_DEFECTO);
        String usuario = valor(request.getParameter("usuario"), "monster");
        String clave = valor(request.getParameter("clave"), "monster9");

        try {
            ApiClient api = new ApiClient(baseUrl);
            String token = api.login(usuario, clave);
            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("token", token);
            sesion.setAttribute("usuario", usuario);
            sesion.setAttribute("baseUrl", baseUrl);
            response.sendRedirect(request.getContextPath() + "/convertir");
        } catch (ApiException e) {
            request.setAttribute("error", "No fue posible iniciar sesion: " + e.getMessage());
            request.setAttribute("baseUrl", baseUrl);
            request.getRequestDispatcher("/WEB-INF/vista/login.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Error de conexion con el servidor: " + e.getMessage());
            request.setAttribute("baseUrl", baseUrl);
            request.getRequestDispatcher("/WEB-INF/vista/login.jsp").forward(request, response);
        }
    }

    private static String valor(String valor, String porDefecto) {
        return valor == null || valor.isBlank() ? porDefecto : valor.trim();
    }
}
