package ec.edu.monster.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginModeloTest {

    @Test
    void debeCrearModeloConConstructorCompleto() {
        LoginModelo modelo = new LoginModelo("Monster", "Monster9");

        assertEquals("Monster", modelo.getUsuario());
        assertEquals("Monster9", modelo.getContrasenia());
    }

    @Test
    void debeActualizarCamposConSetters() {
        LoginModelo modelo = new LoginModelo();

        modelo.setUsuario("admin");
        modelo.setContrasenia("secreto");

        assertEquals("admin", modelo.getUsuario());
        assertEquals("secreto", modelo.getContrasenia());
    }
}
