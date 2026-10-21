package ec.edu.monster.service;

import ec.edu.monster.controlador.EurekaControlador;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DepositoServlet", urlPatterns = {"/DepositoServlet"})
public class DepositoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obtener los parámetros del formulario
        String cuenta = request.getParameter("cuenta");
        double importe = Double.parseDouble(request.getParameter("importe"));
        
        // Procesar el depósito
        EurekaControlador controlador = new EurekaControlador();
        int estado = controlador.regDeposito(cuenta, importe);
        
        // Determinar el mensaje a mostrar
        String mensaje;
        if (estado == 1) {
            mensaje = "Depósito realizado satisfactoriamente!";
        } else {
            mensaje = "Error en el proceso, revise sus datos.";
        }
        
        // Establecer el mensaje como atributo de la solicitud
        request.setAttribute("mensaje", mensaje);
        
        // Redirigir a la página de resultados
        request.getRequestDispatcher("deposito.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    @Override
    public String getServletInfo() {
        return "Servlet para realizar depósitos";
    }
}
