package ec.edu.monster.conversion.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud de autenticacion.
 */
public record LoginRequest(
        @NotBlank(message = "El usuario es obligatorio") String usuario,
        @NotBlank(message = "La clave es obligatoria") String clave) {
}
