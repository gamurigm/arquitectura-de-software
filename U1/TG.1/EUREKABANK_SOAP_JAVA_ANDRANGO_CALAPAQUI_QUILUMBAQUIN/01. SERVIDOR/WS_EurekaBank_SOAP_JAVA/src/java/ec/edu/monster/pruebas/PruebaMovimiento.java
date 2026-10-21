/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.pruebas;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.servicios.EurekaService;
import java.util.List;
/**
 *
 * @author leito
 */
public class PruebaMovimiento {
    public static void main(String[] args) {
        try {
            //dato de la prueba
            String cuenta = "00100001";
            //proceso
            EurekaService service =new EurekaService();
            List<Movimiento> lista = service.leerMovimientos(cuenta);
            //reporte
            for(Movimiento r : lista){
                System.out.println(r.getCuenta()+ " - " + r.getNromov()+ " - "+ r.getFecha()+ " - "+ r.getTipo()+ " - " + r.getAccion() + " - " + r.getImporte());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }    }
}