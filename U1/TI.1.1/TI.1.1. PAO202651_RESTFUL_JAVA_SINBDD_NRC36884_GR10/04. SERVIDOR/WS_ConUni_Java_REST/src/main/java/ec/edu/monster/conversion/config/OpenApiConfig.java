package ec.edu.monster.conversion.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion del contrato OpenAPI (Swagger) con esquema de seguridad Bearer.
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA = "bearerAuth";

    @Bean
    public OpenAPI conversionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST Conversion de Unidades Monster")
                        .version("1.0.0")
                        .description("Servicios REST para autenticacion con token y conversion de unidades "
                                + "de longitud, masa y temperatura. Java 21 + Spring Boot, sin base de datos.")
                        .contact(new Contact().name("Grupo 10 - NRC 36884").email("monster@espe.edu.ec")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA))
                .components(new Components().addSecuritySchemes(ESQUEMA,
                        new SecurityScheme()
                                .name(ESQUEMA)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("UUID")
                                .description("Token emitido por POST /api/auth/login")));
    }
}
