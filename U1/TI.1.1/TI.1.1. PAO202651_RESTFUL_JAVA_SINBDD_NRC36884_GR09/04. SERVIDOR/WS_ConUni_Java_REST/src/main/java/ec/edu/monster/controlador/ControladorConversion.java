package ec.edu.monster.controlador;

import ec.edu.monster.modelo.dto.SolicitudConversion;
import ec.edu.monster.modelo.dto.RespuestaConversion;
import ec.edu.monster.modelo.Magnitud;
import ec.edu.monster.servicios.ServicioConversion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rutas del módulo de conversiones. Requieren un token válido (filtro de autorización).
 */
@RestController
@RequestMapping("/api/conversiones")
@Tag(name = "Conversiones", description = "Conversion de unidades de longitud, masa y temperatura")
public class ControladorConversion {

    private final ServicioConversion conversionService;

    public ControladorConversion(ServicioConversion conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/longitud")
    @Operation(summary = "Convertir longitud",
            description = "Unidades: milimetros, centimetros, metros, kilometros, pies, yardas, millas.")
    public RespuestaConversion longitud(@Valid @RequestBody SolicitudConversion solicitud) {
        return conversionService.convertir(Magnitud.LONGITUD, solicitud);
    }

    @PostMapping("/masa")
    @Operation(summary = "Convertir masa",
            description = "Unidades: miligramos, gramos, kilogramos, toneladas, onzas, libras, quintales.")
    public RespuestaConversion masa(@Valid @RequestBody SolicitudConversion solicitud) {
        return conversionService.convertir(Magnitud.MASA, solicitud);
    }

    @PostMapping("/temperatura")
    @Operation(summary = "Convertir temperatura",
            description = "Unidades: celsius, fahrenheit, kelvin, rankine, newton, reaumur.")
    public RespuestaConversion temperatura(@Valid @RequestBody SolicitudConversion solicitud) {
        return conversionService.convertir(Magnitud.TEMPERATURA, solicitud);
    }
}
