/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.ws;

import ec.edu.monster.controlador.Login;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.ResultadoOperacion;
import ec.edu.monster.servicios.EurekaService;
import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

/**
 *
 * @author leito
 */
@WebService(serviceName = "WSEureka")
public class WSEureka {

    /**
     * Web service operation
     * @param cuenta
     * @return Retorna la lista de movimientos de una cuenta
     */
    @WebMethod(operationName = "traerMovimientos")
    @WebResult(name = "movimiento")
    public List<Movimiento> traerMovimientos(@WebParam(name = "cuenta") String cuenta) {
        List<Movimiento> lista;
        
        //proceso
        try {
            //recuperar moviemintos
            EurekaService service =new EurekaService();
            lista = service.leerMovimientos(cuenta);
        } catch (Exception e) {
            //en caso de error, retorne una lista vacia
            lista = new ArrayList<>();
        }
        
        //retorno
        return lista;
    }

    /**
     * Web service operation
     * @param cuenta
     * @param importe
     * @return Estado, 1 o -1
     */
    @WebMethod(operationName = "regDeposito")
    @WebResult(name = "resultado")
    public int regDeposito(@WebParam(name = "cuenta") String cuenta, @WebParam(name = "importe") double importe) {
        // Validación de parámetros
        if (cuenta == null || cuenta.isEmpty()) {
            return -1;
        }
        if (importe <= 0) {
            return -1;
        }

        // Proceso de registro de depósito
        String codEmp = "0001";
        try {
            EurekaService service = new EurekaService();
            service.registrarDeposito(cuenta, importe, codEmp);
            return 1;
        } catch (Exception e) {
            // Registrar el error para diagnóstico
            System.err.println("Error al registrar el depósito: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    @WebMethod(operationName = "login")
    @WebResult(name = "login")
    public int iniciarSesion(@WebParam(name = "usuario") String usuario, @WebParam(name = "contrasena") String contrasena) {
        try {
            Login service = new Login();
            
            if(service.IniciarSesion(usuario, contrasena)){
                return 1;
            }else{
                return -1;
            }
        } catch (Exception e) {
            // Registrar el error para diagnóstico
            System.err.println("Error al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }


}
