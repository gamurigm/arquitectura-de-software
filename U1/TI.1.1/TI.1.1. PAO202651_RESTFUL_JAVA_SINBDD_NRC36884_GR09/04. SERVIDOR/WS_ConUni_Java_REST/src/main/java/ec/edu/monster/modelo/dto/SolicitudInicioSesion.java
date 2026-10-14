package ec.edu.monster.modelo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Solicitud de autenticacion.
 */
public record SolicitudInicioSesion(
        @NotBlank(message = "El usuario es obligatorio") String usuario,
        @NotBlank(message = "La clave es obligatoria") String clave) {
}
