package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.RespuestaAutenticacion;
import ec.edu.monster.modelo.RespuestaDeposito;
import ec.edu.monster.modelo.RespuestaTransferencia;
import ec.edu.monster.modelo.SolicitudTransferencia;
import ec.edu.monster.modelo.SolicitudDeposito;
import ec.edu.monster.modelo.SolicitudLogin;
import ec.edu.monster.servicios.EurekaBankService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("api/eureka")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ControladorEureka {
    private static final Logger LOGGER = Logger.getLogger(ControladorEureka.class.getName());
    private final EurekaBankService servicio = new EurekaBankService();

    @POST
    @Path("login")
    public Response iniciarSesion(SolicitudLogin solicitud) {
        if (solicitud == null) return Response.status(400).entity(new RespuestaAutenticacion(false)).build();
        try {
            String token = servicio.iniciarSesion(solicitud.getUsuario(), solicitud.getContrasena());
            return Response.ok(new RespuestaAutenticacion(token != null, token)).build();
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "No se pudo validar el acceso.", e);
            return Response.serverError().entity(new RespuestaAutenticacion(false)).build();
        }
    }

    @GET
    @Path("movimientos")
    public Response consultarMovimientos(@QueryParam("cuenta") String cuenta) {
        if (cuenta == null || cuenta.strip().length() != 8) return Response.status(400).entity(List.of()).build();
        try {
            List<Movimiento> movimientos = servicio.traerMovimientos(cuenta);
            return Response.ok(movimientos).build();
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "No se pudieron consultar los movimientos.", e);
            return Response.serverError().entity(List.of()).build();
        }
    }

    @POST
    @Path("depositos")
    public Response registrarDeposito(SolicitudDeposito solicitud) {
        if (solicitud == null) return Response.status(400).entity(new RespuestaDeposito(false)).build();
        try {
            return Response.ok(new RespuestaDeposito(
                    servicio.registrarDeposito(solicitud.getCuenta(), solicitud.getImporte()))).build();
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "No se pudo registrar el depósito.", e);
            return Response.serverError().entity(new RespuestaDeposito(false)).build();
        }
    }

    @POST
    @Path("transferencias")
    public Response transferir(@HeaderParam("Authorization") String authorization, SolicitudTransferencia solicitud) {
        try {
            Boolean registrada = servicio.transferir(extraerToken(authorization), solicitud);
            if (registrada == null) return Response.status(Response.Status.UNAUTHORIZED).build();
            return Response.ok(new RespuestaTransferencia(registrada)).build();
        } catch (SQLException | RuntimeException e) {
            LOGGER.log(Level.SEVERE, "No se pudo registrar la transferencia.", e);
            return Response.serverError().entity(new RespuestaTransferencia(false)).build();
        }
    }

    @POST
    @Path("logout")
    public Response cerrarSesion(@HeaderParam("Authorization") String authorization) {
        servicio.cerrarSesion(extraerToken(authorization));
        return Response.ok(java.util.Map.of("cerrada", true)).build();
    }

    private static String extraerToken(String authorization) {
        return authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)
                ? authorization.substring(7).strip() : null;
    }
}
