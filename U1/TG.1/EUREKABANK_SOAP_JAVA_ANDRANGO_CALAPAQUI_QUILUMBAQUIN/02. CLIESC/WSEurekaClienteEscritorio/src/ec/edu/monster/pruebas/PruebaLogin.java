package ec.edu.monster.pruebas;

import ec.edu.monster.service.EurekaService; // Asegúrate de importar el servicio
import java.util.List;

/**
 *
 * @author genes
 */
public class PruebaLogin {
    
    public static void main(String[] args) {
        try {
            // Datos de la prueba
            String usuario = "MONSTER";
            String contrasena = "MONSTER9";

            // Llamada al servicio Eureka para hacer login
            int resultadoLogin = EurekaService.login(usuario, contrasena);

            // Mostrar el resultado del login
            if (resultadoLogin == 1) {
                System.out.println("Login exitoso");
            } else {
                System.out.println("Login fallido");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
