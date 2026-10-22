package ec.edu.monster.controlador;

import ec.edu.monster.servicios.ClienteEurekaRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@WebServlet("/transferencia")
public class ServletTransferencia extends HttpServlet {
    private final ClienteEurekaRest cliente = new ClienteEurekaRest();

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession sesion = request.getSession(false);
        if (sesion == null || sesion.getAttribute("usuario") == null || sesion.getAttribute("token") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String esperado = (String) sesion.getAttribute("csrfToken");
        String recibido = request.getParameter("csrfToken");
        if (esperado == null || recibido == null || !java.security.MessageDigest.isEqual(
                esperado.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                recibido.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String origen = request.getParameter("cuentaOrigen");
        String destino = request.getParameter("cuentaDestino");
        String clave = request.getParameter("claveOperacion");
        request.setAttribute("usuario", sesion.getAttribute("usuario"));
        request.setAttribute("csrfToken", esperado);
        request.setAttribute("claveOperacion", clave);
        request.setAttribute("cuenta", destino == null ? "" : destino);
        try {
            BigDecimal importe = new BigDecimal(request.getParameter("importe"));
            UUID.fromString(clave);
            if (origen == null || !origen.matches("[0-9]{8}") || destino == null
                    || !destino.matches("[0-9]{8}") || origen.equals(destino)
                    || importe.signum() <= 0 || importe.scale() > 2 || importe.precision() > 12) {
                request.setAttribute("mensaje", "Use cuentas distintas de ocho dígitos e importe positivo de hasta dos decimales.");
                request.setAttribute("error", true);
            } else if (cliente.transferir((String) sesion.getAttribute("token"), origen, destino, importe, clave)) {
                request.setAttribute("mensaje", "Transferencia registrada: débito y crédito confirmados.");
                request.setAttribute("claveOperacion", UUID.randomUUID().toString());
                request.setAttribute("movimientos", cliente.traerMovimientos(destino));
            } else {
                request.setAttribute("mensaje", "Transferencia rechazada. Revise cuentas, moneda, saldo e importe.");
                request.setAttribute("error", true);
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("mensaje", "La solicitud de transferencia no es válida.");
            request.setAttribute("error", true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            request.setAttribute("mensaje", "No se confirmó el resultado. Reenvíe la misma solicitud para evitar duplicados.");
            request.setAttribute("error", true);
        } catch (Exception e) {
            request.setAttribute("mensaje", "No se confirmó el resultado. Reenvíe la misma solicitud para evitar duplicados.");
            request.setAttribute("error", true);
        }
        request.getRequestDispatcher("/WEB-INF/vista/inicio.jsp").forward(request, response);
    }
}
