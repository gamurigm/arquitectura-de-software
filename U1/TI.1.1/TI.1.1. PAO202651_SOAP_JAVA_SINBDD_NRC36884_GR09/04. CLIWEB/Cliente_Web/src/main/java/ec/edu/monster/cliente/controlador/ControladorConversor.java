package ec.edu.monster.cliente.controlador;

import ec.edu.monster.cliente.servicios.ClienteSOAP;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/convertir", "/ConversorServlet"})
public class ControladorConversor extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        HttpSession session = request.getSession(false);
        String token = session == null ? null : (String) session.getAttribute("miToken");
        if (token == null || token.isBlank()) {
            expirarSesion(session);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("ERROR_SESSION");
            return;
        }

        try {
            String tipo = request.getParameter("cmbTipo");
            String origen = request.getParameter("txtOrigen");
            String destino = request.getParameter("txtDestino");
            double valor = Double.parseDouble(request.getParameter("txtValor"));
            if (!Double.isFinite(valor) || origen == null || destino == null) {
                throw new IllegalArgumentException("Revise el valor y las unidades seleccionadas.");
            }
            double resultado = new ClienteSOAP(token).convertir(tipo, valor, origen, destino);
            response.getWriter().write(Double.toString(resultado));
        } catch (RuntimeException ex) {
            if (esTokenExpirado(ex)) {
                expirarSesion(session);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("ERROR_SESSION");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("ERROR: " + mensajeSeguro(ex));
            }
        }
    }

    private static boolean esTokenExpirado(Throwable ex) {
        for (Throwable causa = ex; causa != null; causa = causa.getCause()) {
            String mensaje = causa.getMessage();
            if (mensaje != null && (mensaje.toLowerCase().contains("token inválido")
                    || mensaje.toLowerCase().contains("no se encontró el token")
                    || mensaje.toLowerCase().contains("se requiere un encabezado soap"))) {
                return true;
            }
        }
        return false;
    }

    private static String mensajeSeguro(Exception ex) {
        String mensaje = ex.getMessage();
        return mensaje == null || mensaje.isBlank() ? "No fue posible realizar la conversión." : mensaje;
    }

    private static void expirarSesion(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
