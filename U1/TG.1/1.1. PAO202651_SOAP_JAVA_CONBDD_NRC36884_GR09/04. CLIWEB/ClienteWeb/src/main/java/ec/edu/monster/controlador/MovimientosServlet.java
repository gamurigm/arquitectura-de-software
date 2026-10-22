package ec.edu.monster.controlador;

import ec.edu.monster.service.EurekaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/movimientos")
public final class MovimientosServlet extends ControladorProtegido {
    private final EurekaService servicio = new EurekaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (exigirSesion(request, response)) {
            request.getRequestDispatcher("/WEB-INF/jsp/movimientos.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!exigirSesion(request, response)) return;
        request.setCharacterEncoding("UTF-8");
        String cuenta = request.getParameter("cuenta");
        if (cuenta == null || cuenta.isBlank() || cuenta.strip().length() > 8) {
            request.setAttribute("error", "Ingrese una cuenta válida de hasta 8 caracteres.");
            doGet(request, response);
            return;
        }
        try {
            request.setAttribute("cuenta", cuenta.strip());
            request.setAttribute("movimientos", servicio.traerMovimientos(cuenta.strip()));
        } catch (RuntimeException e) {
            request.setAttribute("error", "No se pudieron consultar los movimientos.");
        }
        request.getRequestDispatcher("/WEB-INF/jsp/movimientos.jsp").forward(request, response);
    }
}
