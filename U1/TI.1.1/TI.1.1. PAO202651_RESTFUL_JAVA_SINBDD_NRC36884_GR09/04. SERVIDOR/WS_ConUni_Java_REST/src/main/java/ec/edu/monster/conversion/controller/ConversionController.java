package ec.edu.monster.conversion.controller;

import ec.edu.monster.conversion.dto.ConversionRequest;
import ec.edu.monster.conversion.dto.ConversionResponse;
import ec.edu.monster.conversion.model.Magnitud;
import ec.edu.monster.conversion.service.ConversionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints del modulo de conversiones. Requieren token valido (filtro de autorizacion).
 */
@RestController
@RequestMapping("/api/conversiones")
@Tag(name = "Conversiones", description = "Conversion de unidades de longitud, masa y temperatura")
public class ConversionController {

    private final ConversionService conversionService;

    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/longitud")
    @Operation(summary = "Convertir longitud",
            description = "Unidades: centimetros, metros, pies, yardas, millas.")
    public ConversionResponse longitud(@Valid @RequestBody ConversionRequest request) {
        return conversionService.convertir(Magnitud.LONGITUD, request);
    }

    @PostMapping("/masa")
    @Operation(summary = "Convertir masa",
            description = "Unidades: gramos, kilogramos, onzas, libras, quintales.")
    public ConversionResponse masa(@Valid @RequestBody ConversionRequest request) {
        return conversionService.convertir(Magnitud.MASA, request);
    }

    @PostMapping("/temperatura")
    @Operation(summary = "Convertir temperatura",
            description = "Unidades: fahrenheit, kelvin, celsius, newton, reaumur.")
    public ConversionResponse temperatura(@Valid @RequestBody ConversionRequest request) {
        return conversionService.convertir(Magnitud.TEMPERATURA, request);
    }
}
