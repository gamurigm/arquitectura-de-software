package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.servicios.ClienteEurekaRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/deposito")
public class ServletDeposito extends HttpServlet {
    private final ClienteEurekaRest cliente = new ClienteEurekaRest();

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        request.setCharacterEncoding("UTF-8");
        String cuenta = request.getParameter("cuenta");
        request.setAttribute("usuario", request.getSession(false).getAttribute("usuario"));
        request.setAttribute("cuenta", cuenta == null ? "" : cuenta);
        String mensaje;
        boolean error = false;
        try {
            BigDecimal importe = new BigDecimal(request.getParameter("importe"));
            if (cuenta == null || !cuenta.matches("[0-9]{8}") || importe.signum() <= 0 || importe.scale() > 2) {
                mensaje = "La cuenta o el importe no son válidos. Use un importe positivo de hasta dos decimales.";
                error = true;
            } else if (cliente.registrarDeposito((String) request.getSession(false).getAttribute("token"), cuenta, importe)) {
                mensaje = "Depósito registrado.";
                List<Movimiento> movimientos = cliente.traerMovimientos(
                        (String) request.getSession(false).getAttribute("token"), cuenta);
                request.setAttribute("movimientos", movimientos);
            } else {
                mensaje = "No se pudo registrar el depósito. Revise que la cuenta esté activa.";
                error = true;
            }
        } catch (NumberFormatException e) {
            mensaje = "Ingrese un importe decimal válido.";
            error = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            mensaje = "La solicitud fue interrumpida.";
            error = true;
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("HTTP 401")) {
                request.getSession(false).invalidate();
                response.sendRedirect(request.getContextPath() + "/login?expirada=1");
                return;
            }
            mensaje = "No se pudo comunicar con EurekaBank.";
            error = true;
        }
        request.setAttribute("mensaje", mensaje);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/WEB-INF/vista/inicio.jsp").forward(request, response);
    }
}
