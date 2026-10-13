package ec.edu.monster.cliweb;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servlet de conversiones. Consume /api/conversiones/* usando el token de la sesion.
 */
@WebServlet(name = "ConversionServlet", urlPatterns = {"/convertir"})
public class ConversionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (sinSesion(request, response)) {
            return;
        }
        cargarCatalogo(request);
        request.getRequestDispatcher("/conversion.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (sinSesion(request, response)) {
            return;
        }

        cargarCatalogo(request);
        String magnitud = request.getParameter("magnitud");
        String origen = request.getParameter("origen");
        String destino = request.getParameter("destino");
        String valorTexto = request.getParameter("valor");
        request.setAttribute("magnitud", magnitud);
        request.setAttribute("origen", origen);
        request.setAttribute("destino", destino);
        request.setAttribute("valor", valorTexto);

        try {
            double valor = Double.parseDouble(valorTexto.trim().replace(",", "."));
            ApiClient api = new ApiClient((String) sesion.getAttribute("baseUrl"));
            JsonNode respuesta = api.convertir(magnitud, valor, origen, destino,
                    (String) sesion.getAttribute("token"));
            request.setAttribute("resultado", respuesta.path("resultado").asDouble());
            request.setAttribute("mensaje", respuesta.path("mensaje").asText());
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Ingrese un valor numerico valido.");
        } catch (ApiException e) {
            request.setAttribute("error", e.getMessage());
            if (e.esNoAutorizado()) {
                sesion.invalidate();
                request.setAttribute("error", "La sesion expiro. Inicie sesion nuevamente.");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error al consumir el servicio: " + e.getMessage());
        }
        request.getRequestDispatcher("/conversion.jsp").forward(request, response);
    }

    private boolean sinSesion(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("token") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return true;
        }
        return false;
    }

    private void cargarCatalogo(HttpServletRequest request) {
        try {
            HttpSession sesion = request.getSession(false);
            ApiClient api = new ApiClient((String) sesion.getAttribute("baseUrl"));
            JsonNode magnitudes = api.catalogo().path("magnitudes");
            Map<String, java.util.List<String>> catalogo = new LinkedHashMap<>();
            magnitudes.fields().forEachRemaining(entrada -> {
                java.util.List<String> unidades = new java.util.ArrayList<>();
                entrada.getValue().forEach(n -> unidades.add(n.asText()));
                catalogo.put(entrada.getKey(), unidades);
            });
            request.setAttribute("catalogo", catalogo);
        } catch (Exception ignored) {
            // el JSP usara su lista por defecto
        }
    }
}
