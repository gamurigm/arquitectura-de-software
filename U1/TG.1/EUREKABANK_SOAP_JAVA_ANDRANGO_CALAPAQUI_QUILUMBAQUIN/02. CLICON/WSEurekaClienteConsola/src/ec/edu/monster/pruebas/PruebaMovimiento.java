package ec.edu.monster.pruebas;

import ec.edu.monster.service.EurekaService;
import ec.edu.monster.ws.Movimiento;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;

public class PruebaMovimiento {

    public static void main(String[] args) {
        try {
            // Dato de la prueba
            String cuenta = "00100001";
            
            // Proceso
            EurekaService service = new EurekaService();
            List<Movimiento> lista = service.traerMovimientos(cuenta);
            
            // Definir el formato de fecha
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            // Reporte
            for (Movimiento r : lista) {
                // Obtener la fecha desde el XMLGregorianCalendar
                XMLGregorianCalendar fechaXml = r.getFecha();
                
                // Convertir la fecha a Date
                Date fecha = fechaXml.toGregorianCalendar().getTime();
                
                // Formatear la fecha
                String fechaFormateada = sdf.format(fecha);
                
                // Imprimir el movimiento
                System.out.println(r.getCuenta() + " - " + r.getNromov() + " - " + fechaFormateada + " - " + r.getTipo() + " - " + r.getAccion() + " - " + r.getImporte());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
