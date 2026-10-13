package ec.edu.monster.conversion.controller;

import ec.edu.monster.conversion.dto.LoginRequest;
import ec.edu.monster.conversion.dto.TokenResponse;
import ec.edu.monster.conversion.security.TokenStore;
import ec.edu.monster.conversion.service.AuthService;
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
public class AuthController {

    private final AuthService authService;
    private final TokenStore tokenStore;

    public AuthController(AuthService authService, TokenStore tokenStore) {
        this.authService = authService;
        this.tokenStore = tokenStore;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion",
            description = "Valida las credenciales (monster / monster9) y emite un token temporal de 30 minutos.")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.usuario(), request.clave());
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion",
            description = "Revoca el token enviado en el encabezado Authorization: Bearer <token>.")
    public ResponseEntity<Map<String, Object>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestHeader(value = "X-Auth-Token", required = false) String tokenAlterno) {
        String token = authorization != null && authorization.regionMatches(true, 0, "Bearer ", 0, 7)
                ? authorization.substring(7).trim()
                : tokenAlterno;
        tokenStore.revocar(token);
        return ResponseEntity.ok(Map.of("exito", true, "mensaje", "Sesion cerrada correctamente."));
    }
}
