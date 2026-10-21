package ec.edu.monster;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PruebaInicializacionServlet {

    @Test
    void laAplicacionPuedeIniciarseComoServlet() {
        assertTrue(SpringBootServletInitializer.class.isAssignableFrom(AplicacionRestConversiones.class));
    }
}
