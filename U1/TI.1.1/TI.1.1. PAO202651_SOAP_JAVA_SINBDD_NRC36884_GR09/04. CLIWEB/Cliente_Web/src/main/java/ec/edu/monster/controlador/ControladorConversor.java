package ec.edu.monster.controlador;

import ec.edu.monster.servicios.ClienteSOAP;
import ec.edu.monster.modelo.ConversionModelo;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/convertir", "/ConversorServlet"})
public class ControladorConversor extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest solicitud, HttpServletResponse respuesta) throws IOException {
        respuesta.setContentType("text/plain;charset=UTF-8");
        HttpSession session = solicitud.getSession(false);
        String token = session == null ? null : (String) session.getAttribute("miToken");
        if (token == null || token.isBlank()) {
            expirarSesion(session);
            respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            respuesta.getWriter().write("ERROR_SESSION");
            return;
        }

        try {
            String tipo = solicitud.getParameter("cmbTipo");
            String origen = solicitud.getParameter("txtOrigen");
            String destino = solicitud.getParameter("txtDestino");
            String valorTexto = solicitud.getParameter("txtValor");
            if (tipo == null || !tipo.matches("longitud|masa|temperatura")
                    || origen == null || origen.isBlank() || destino == null || destino.isBlank()
                    || valorTexto == null || valorTexto.isBlank()) {
                throw new IllegalArgumentException("Revise el valor y las unidades seleccionadas.");
            }
            double valor = Double.parseDouble(valorTexto);
            if (!Double.isFinite(valor)) {
                throw new IllegalArgumentException("El valor debe ser un número finito.");
            }
            if ("masa".equals(tipo) && valor < 0) {
                throw new IllegalArgumentException("La masa no puede ser negativa.");
            }
            ConversionModelo modelo = new ConversionModelo(tipo, valor, origen, destino);
            double resultado = new ClienteSOAP(token).convertir(
                    modelo.getTipo(), modelo.getValor(), modelo.getOrigen(), modelo.getDestino());
            if (!Double.isFinite(resultado)) {
                throw new IllegalArgumentException("El resultado excede el rango permitido.");
            }
            respuesta.getWriter().write(Double.toString(resultado));
        } catch (RuntimeException ex) {
            if (esTokenExpirado(ex)) {
                expirarSesion(session);
                respuesta.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                respuesta.getWriter().write("ERROR_SESSION");
            } else {
                respuesta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                respuesta.getWriter().write("ERROR: " + mensajeSeguro(ex));
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
