package ec.edu.monster.conversion.controlador;

import ec.edu.monster.conversion.modelo.dto.CatalogoResponse;
import ec.edu.monster.conversion.servicios.UnidadCatalog;
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
public class CatalogoController {

    private final UnidadCatalog catalogo;

    public CatalogoController(UnidadCatalog catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    @Operation(summary = "Listar catalogo de unidades",
            description = "Devuelve las magnitudes y sus unidades disponibles. Recurso publico.")
    public CatalogoResponse listar() {
        return new CatalogoResponse(true, catalogo.catalogo(), "Catalogo de unidades disponibles.");
    }
}
