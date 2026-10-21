/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.pruebas;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.servicios.EurekaService;
import ec.edu.monster.controlador.Login;
import java.util.List;

/**
 *
 * @author genes
 */
public class PruebaLogin {
    
    public static void main(String[] args) {
        try {
            //dato de la prueba
            String usuario = "MONSTER";
            String contrasena = "MONSTER9";
            //proceso
            Login service =new Login();
            System.out.println(service.IniciarSesion(usuario, contrasena));
            
        } catch (Exception e) {
            e.printStackTrace();
        }    }
}
 