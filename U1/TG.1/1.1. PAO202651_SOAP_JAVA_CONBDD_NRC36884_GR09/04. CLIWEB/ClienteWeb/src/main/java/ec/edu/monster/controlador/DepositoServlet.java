package ec.edu.monster.controlador;

import ec.edu.monster.service.EurekaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet("/depositos")
public final class DepositoServlet extends ControladorProtegido {
    private final EurekaService servicio = new EurekaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (exigirSesion(request, response)) {
            request.setAttribute("csrf", tokenCsrf(request));
            request.getRequestDispatcher("/WEB-INF/jsp/depositos.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!exigirSesion(request, response)) return;
        if (!validarCsrf(request, response)) return;
        request.setCharacterEncoding("UTF-8");
        String cuenta = request.getParameter("cuenta");
        String textoImporte = request.getParameter("importe");
        try {
            if (cuenta == null || cuenta.isBlank() || cuenta.strip().length() > 8) {
                throw new IllegalArgumentException("Ingrese una cuenta válida de hasta 8 caracteres.");
            }
            BigDecimal importe = new BigDecimal(textoImporte.strip()).setScale(2, RoundingMode.UNNECESSARY);
            if (importe.signum() <= 0 || importe.precision() > 12) {
                throw new IllegalArgumentException("El importe debe ser positivo y no superar 10 dígitos.");
            }
            if (!servicio.registrarDeposito(cuenta.strip(), importe)) {
                throw new IllegalArgumentException("El servidor rechazó el depósito.");
            }
            response.sendRedirect(request.getContextPath() + "/depositos?resultado=registrado");
            return;
        } catch (NumberFormatException | ArithmeticException e) {
            request.setAttribute("error", "Ingrese un importe válido con máximo dos decimales.");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            request.setAttribute("error", "No se confirmó el resultado del depósito. Revise movimientos antes de reintentar.");
        }
        request.getRequestDispatcher("/WEB-INF/jsp/depositos.jsp").forward(request, response);
    }
}
