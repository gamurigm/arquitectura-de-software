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
        @SuppressWarnings("unchecked") java.util.List<ec.edu.monster.modelo.Cuenta> cuentas =
                (java.util.List<ec.edu.monster.modelo.Cuenta>) sesion.getAttribute("cuentas");
        request.setAttribute("usuario", sesion.getAttribute("usuario"));
        request.setAttribute("csrfToken", esperado);
        request.setAttribute("claveOperacion", clave);
        request.setAttribute("cuenta", destino == null ? "" : destino);
        try {
            BigDecimal importe = new BigDecimal(request.getParameter("importe"));
            UUID.fromString(clave);
            var cuentaOrigen = cuentas.stream().filter(c -> c.numero().equals(origen)).findFirst().orElse(null);
            var cuentaDestino = cuentas.stream().filter(c -> c.numero().equals(destino)).findFirst().orElse(null);
            if (cuentaOrigen == null || cuentaDestino == null || origen.equals(destino)
                    || !cuentaOrigen.moneda().equals(cuentaDestino.moneda())
                    || importe.signum() <= 0 || importe.scale() > 2 || importe.precision() > 12) {
                request.setAttribute("mensaje", "Use cuentas distintas de ocho dígitos e importe positivo de hasta dos decimales.");
                request.setAttribute("error", true);
            } else if (cliente.transferir((String) sesion.getAttribute("token"), origen, destino, importe, clave)) {
                request.setAttribute("mensaje", "Transferencia registrada: débito y crédito confirmados.");
                request.setAttribute("claveOperacion", UUID.randomUUID().toString());
                request.setAttribute("movimientos", cliente.traerMovimientos((String) sesion.getAttribute("token"), origen));
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
            if (e.getMessage() != null && e.getMessage().contains("HTTP 401")) {
                sesion.invalidate();
                response.sendRedirect(request.getContextPath() + "/login?expirada=1");
                return;
            }
            request.setAttribute("mensaje", "No se confirmó el resultado. Reenvíe la misma solicitud para evitar duplicados.");
            request.setAttribute("error", true);
        }
        request.getRequestDispatcher("/WEB-INF/vista/inicio.jsp").forward(request, response);
    }
}
