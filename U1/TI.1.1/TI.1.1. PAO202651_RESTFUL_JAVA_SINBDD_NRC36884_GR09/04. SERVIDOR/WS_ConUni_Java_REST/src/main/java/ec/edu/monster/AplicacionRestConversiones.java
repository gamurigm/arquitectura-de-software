package ec.edu.monster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST "Conversion de Unidades Monster".
 * Java 21 LTS + Spring Boot 3.5.x + Spring Web. Sin base de datos.
 */
@SpringBootApplication
public class AplicacionRestConversiones {

    public static void main(String[] args) {
        SpringApplication.run(AplicacionRestConversiones.class, args);
    }
}
