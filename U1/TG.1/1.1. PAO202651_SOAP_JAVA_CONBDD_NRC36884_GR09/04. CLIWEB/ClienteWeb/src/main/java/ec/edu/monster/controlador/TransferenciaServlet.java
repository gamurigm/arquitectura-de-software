package ec.edu.monster.controlador;

import ec.edu.monster.service.EurekaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@WebServlet("/transferencias")
public final class TransferenciaServlet extends ControladorProtegido {
    private final EurekaService servicio = new EurekaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!exigirSesion(request, response)) return;
        var sesion = request.getSession(false);
        String clave = (String) sesion.getAttribute("transferencia.clave");
        if (clave == null) {
            clave = UUID.randomUUID().toString();
            sesion.setAttribute("transferencia.clave", clave);
        }
        request.setAttribute("claveOperacion", clave);
        request.setAttribute("cuentaOrigen", sesion.getAttribute("transferencia.origen"));
        request.setAttribute("cuentaDestino", sesion.getAttribute("transferencia.destino"));
        request.setAttribute("importe", sesion.getAttribute("transferencia.importe"));
        request.getRequestDispatcher("/WEB-INF/jsp/transferencias.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!exigirSesion(request, response) || !validarCsrf(request, response)) return;
        request.setCharacterEncoding("UTF-8");
        var sesion = request.getSession(false);
        String origen = valor(request, "cuentaOrigen");
        String destino = valor(request, "cuentaDestino");
        String textoImporte = valor(request, "importe");
        String huella = origen + "\u001f" + destino + "\u001f" + textoImporte;
        UUID publicada;
        try { publicada = UUID.fromString(valor(request, "claveOperacion")); }
        catch (IllegalArgumentException e) { publicada = UUID.randomUUID(); }
        String claveSesion = (String) sesion.getAttribute("transferencia.clave");
        String huellaSesion = (String) sesion.getAttribute("transferencia.huella");
        UUID clave = publicada;
        if (!publicada.toString().equals(claveSesion)
                || (huellaSesion != null && !huellaSesion.equals(huella))) {
            clave = UUID.randomUUID();
        }
        sesion.setAttribute("transferencia.clave", clave.toString());
        sesion.setAttribute("transferencia.huella", huella);
        sesion.setAttribute("transferencia.origen", origen);
        sesion.setAttribute("transferencia.destino", destino);
        sesion.setAttribute("transferencia.importe", textoImporte);
        request.setAttribute("claveOperacion", clave.toString());
        request.setAttribute("cuentaOrigen", origen);
        request.setAttribute("cuentaDestino", destino);
        request.setAttribute("importe", textoImporte);

        try {
            if (!origen.matches("[0-9]{8}") || !destino.matches("[0-9]{8}") || origen.equals(destino))
                throw new IllegalArgumentException("Ingrese cuentas distintas de 8 dígitos.");
            BigDecimal importe = new BigDecimal(textoImporte).setScale(2, RoundingMode.UNNECESSARY);
            if (importe.signum() <= 0 || importe.precision() > 12)
                throw new IllegalArgumentException("El importe debe ser positivo y no superar 10 dígitos.");
            String token = (String) sesion.getAttribute("token");
            if (!servicio.transferir(token, origen, destino, importe, clave))
                throw new IllegalArgumentException("El servicio rechazó la transferencia.");
            sesion.removeAttribute("transferencia.clave");
            sesion.removeAttribute("transferencia.huella");
            sesion.removeAttribute("transferencia.origen");
            sesion.removeAttribute("transferencia.destino");
            sesion.removeAttribute("transferencia.importe");
            response.sendRedirect(request.getContextPath() + "/transferencias?resultado=registrada");
            return;
        } catch (NumberFormatException | ArithmeticException e) {
            request.setAttribute("error", "Ingrese un importe válido con máximo dos decimales.");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
        } catch (RuntimeException | IOException e) {
            request.setAttribute("error", "No se confirmó el resultado. Reintente con los mismos datos.");
        }
        request.getRequestDispatcher("/WEB-INF/jsp/transferencias.jsp").forward(request, response);
    }

    private static String valor(HttpServletRequest request, String nombre) {
        String valor = request.getParameter(nombre);
        return valor == null ? "" : valor.strip();
    }
}
