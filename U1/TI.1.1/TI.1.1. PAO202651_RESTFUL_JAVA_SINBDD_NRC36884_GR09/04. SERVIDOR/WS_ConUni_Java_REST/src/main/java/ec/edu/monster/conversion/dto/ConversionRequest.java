package ec.edu.monster.conversion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Solicitud de conversion: valor numerico, unidad de origen y unidad de destino.
 */
public record ConversionRequest(
        @NotNull(message = "El valor es obligatorio") Double valor,
        @NotBlank(message = "La unidad de origen es obligatoria") String unidadOrigen,
        @NotBlank(message = "La unidad de destino es obligatoria") String unidadDestino) {
}
