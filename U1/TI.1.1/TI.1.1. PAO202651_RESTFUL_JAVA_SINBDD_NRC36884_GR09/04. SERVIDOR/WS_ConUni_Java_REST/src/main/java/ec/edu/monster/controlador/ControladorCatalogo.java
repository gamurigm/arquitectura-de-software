package ec.edu.monster.controlador;

import ec.edu.monster.modelo.dto.RespuestaCatalogo;
import ec.edu.monster.servicios.CatalogoUnidades;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Catalogo publico de unidades disponibles. Ayuda a los clientes a construir sus listas.
 */
@RestController
@RequestMapping("/api/catalogo")
@Tag(name = "Catalogo", description = "Unidades disponibles por magnitud")
public class ControladorCatalogo {

    private final CatalogoUnidades catalogo;

    public ControladorCatalogo(CatalogoUnidades catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    @Operation(summary = "Listar catalogo de unidades",
            description = "Devuelve las magnitudes y sus unidades disponibles. Recurso publico.")
    public RespuestaCatalogo listar() {
        return new RespuestaCatalogo(true, catalogo.obtenerCatalogo(), "Catalogo de unidades disponibles.");
    }
}
