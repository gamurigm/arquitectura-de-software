/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.service;

/**
 *
 * @author chris
 */
public class EurekaService {

    public static int login(java.lang.String usuario, java.lang.String contrasena) {
        ec.edu.monster.ws.WSEureka_Service service = new ec.edu.monster.ws.WSEureka_Service();
        ec.edu.monster.ws.WSEureka port = service.getWSEurekaPort();
        return port.login(usuario, contrasena);
    }

    public static int regDeposito(java.lang.String cuenta, double importe) {
        ec.edu.monster.ws.WSEureka_Service service = new ec.edu.monster.ws.WSEureka_Service();
        ec.edu.monster.ws.WSEureka port = service.getWSEurekaPort();
        return port.regDeposito(cuenta, importe);
    }

    public static java.util.List<ec.edu.monster.ws.Movimiento> traerMovimientos(java.lang.String cuenta) {
        ec.edu.monster.ws.WSEureka_Service service = new ec.edu.monster.ws.WSEureka_Service();
        ec.edu.monster.ws.WSEureka port = service.getWSEurekaPort();
        return port.traerMovimientos(cuenta);
    }
}
