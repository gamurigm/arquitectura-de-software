package ec.edu.monster.controlador;

import ec.edu.monster.modelo.SolicitudConversion;
import ec.edu.monster.servicios.ClienteApi;
import ec.edu.monster.servicios.ExcepcionApi;

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
 * Servlet de conversiones. Usa /api/conversiones/* con el token de la sesión.
 */
@WebServlet(name = "ServletConversion", urlPatterns = {"/convertir"})
public class ServletConversion extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        if (sinSesion(solicitud, respuesta)) {
            return;
        }
        cargarCatalogo(solicitud);
        solicitud.getRequestDispatcher("/WEB-INF/vista/conversion.jsp").forward(solicitud, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws ServletException, IOException {
        HttpSession sesion = solicitud.getSession(false);
        if (sinSesion(solicitud, respuesta)) {
            return;
        }

        cargarCatalogo(solicitud);
        String magnitud = solicitud.getParameter("magnitud");
        String origen = solicitud.getParameter("origen");
        String destino = solicitud.getParameter("destino");
        String valorTexto = solicitud.getParameter("valor");
        solicitud.setAttribute("magnitud", magnitud);
        solicitud.setAttribute("origen", origen);
        solicitud.setAttribute("destino", destino);
        solicitud.setAttribute("valor", valorTexto);

        try {
            if (!"longitud".equals(magnitud) && !"masa".equals(magnitud) && !"temperatura".equals(magnitud)) {
                throw new IllegalArgumentException("Seleccione una magnitud válida.");
            }
            if (origen == null || origen.isBlank() || destino == null || destino.isBlank()) {
                throw new IllegalArgumentException("Seleccione las unidades de origen y destino.");
            }
            if (valorTexto == null || valorTexto.isBlank()) {
                throw new IllegalArgumentException("Ingrese el valor que desea convertir.");
            }
            double valor = Double.parseDouble(valorTexto.trim().replace(",", "."));
            if (!Double.isFinite(valor)) {
                throw new IllegalArgumentException("Ingrese un número finito.");
            }
            if ("masa".equals(magnitud) && valor < 0) {
                throw new IllegalArgumentException("La masa no puede ser negativa.");
            }
            ClienteApi api = new ClienteApi((String) sesion.getAttribute("urlBase"));
            SolicitudConversion datosConversion = new SolicitudConversion(magnitud, valor, origen, destino);
            JsonNode datosRespuesta = api.convertir(datosConversion.magnitud(), datosConversion.valor(),
                    datosConversion.origen(), datosConversion.destino(),
                    (String) sesion.getAttribute("token"));
            solicitud.setAttribute("resultado", datosRespuesta.path("resultado").asDouble());
            solicitud.setAttribute("mensaje", datosRespuesta.path("mensaje").asText());
        } catch (NumberFormatException e) {
            solicitud.setAttribute("error", "Ingrese un valor numerico valido.");
        } catch (IllegalArgumentException e) {
            solicitud.setAttribute("error", e.getMessage());
        } catch (ExcepcionApi e) {
            solicitud.setAttribute("error", e.getMessage());
            if (e.esNoAutorizado()) {
                sesion.invalidate();
                solicitud.setAttribute("error", "La sesion expiro. Inicie sesion nuevamente.");
            }
        } catch (Exception e) {
            solicitud.setAttribute("error", "Error al consumir el servicio: " + e.getMessage());
        }
        solicitud.getRequestDispatcher("/WEB-INF/vista/conversion.jsp").forward(solicitud, respuesta);
    }

    private boolean sinSesion(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException {
        HttpSession sesion = solicitud.getSession(false);
        if (sesion == null || sesion.getAttribute("token") == null) {
            respuesta.sendRedirect(solicitud.getContextPath() + "/login");
            return true;
        }
        return false;
    }

    private void cargarCatalogo(HttpServletRequest solicitud) {
        try {
            HttpSession sesion = solicitud.getSession(false);
            ClienteApi api = new ClienteApi((String) sesion.getAttribute("urlBase"));
            JsonNode magnitudes = api.obtenerCatalogo().path("magnitudes");
            Map<String, java.util.List<String>> catalogo = new LinkedHashMap<>();
            magnitudes.properties().forEach(entrada -> {
                java.util.List<String> unidades = new java.util.ArrayList<>();
                entrada.getValue().forEach(n -> unidades.add(n.asText()));
                catalogo.put(entrada.getKey(), unidades);
            });
            solicitud.setAttribute("catalogo", catalogo);
        } catch (Exception ignored) {
            // el JSP usara su lista por defecto
        }
    }
}
