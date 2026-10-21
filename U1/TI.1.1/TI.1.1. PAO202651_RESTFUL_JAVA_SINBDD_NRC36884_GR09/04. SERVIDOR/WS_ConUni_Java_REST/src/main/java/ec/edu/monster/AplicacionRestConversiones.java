package ec.edu.monster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Punto de entrada de la API REST "Conversion de Unidades Monster".
 * Compilado con JDK 25 (bytecode Java 21) + Spring Boot 3.5.x + Spring Web.
 * Sin base de datos.
 */
@SpringBootApplication
public class AplicacionRestConversiones extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AplicacionRestConversiones.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(AplicacionRestConversiones.class, args);
    }
}
