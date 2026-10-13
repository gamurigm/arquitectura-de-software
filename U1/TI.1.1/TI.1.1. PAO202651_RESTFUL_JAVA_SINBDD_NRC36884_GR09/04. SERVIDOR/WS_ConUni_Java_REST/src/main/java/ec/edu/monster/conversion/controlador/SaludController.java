package ec.edu.monster.conversion.controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Endpoint de salud para verificar que el servidor esta en linea.
 */
@RestController
@RequestMapping("/api/salud")
@Tag(name = "Salud", description = "Estado del servidor")
public class SaludController {

    @GetMapping
    @Operation(summary = "Estado del servidor")
    public Map<String, Object> estado() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("exito", true);
        respuesta.put("aplicacion", "WS_ConUni_Java_REST");
        respuesta.put("estado", "EN LINEA");
        respuesta.put("timestamp", OffsetDateTime.now());
        return respuesta;
    }
}
