package ec.edu.monster.controlador;

import ec.edu.monster.modelo.dto.SolicitudInicioSesion;
import ec.edu.monster.modelo.dto.RespuestaToken;
import ec.edu.monster.seguridad.AlmacenTokens;
import ec.edu.monster.servicios.ServicioAutenticacion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoints del modulo de seguridad.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Login y generacion de token de seguridad")
public class ControladorAutenticacion {

    private final ServicioAutenticacion authService;
    private final AlmacenTokens almacenTokens;

    public ControladorAutenticacion(ServicioAutenticacion authService, AlmacenTokens almacenTokens) {
        this.authService = authService;
        this.almacenTokens = almacenTokens;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion",
            description = "Valida las credenciales (Monster / Monster9) y emite un token temporal de 10 minutos.")
    public RespuestaToken iniciarSesion(@Valid @RequestBody SolicitudInicioSesion solicitud) {
        return authService.iniciarSesion(solicitud.usuario(), solicitud.clave());
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion",
            description = "Revoca el token enviado en el encabezado Authorization: Bearer <token>.")
    public ResponseEntity<Map<String, Object>> cerrarSesion(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-Auth-Token", required = false) String tokenAlterno) {
        String token = authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)
                ? authorization.substring(7).trim()
                : tokenAlterno;
        almacenTokens.revocar(token);
        return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Sesion cerrada correctamente."));
    }
}
