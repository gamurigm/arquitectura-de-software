package wseurekaclienteconsola;

import ec.edu.monster.controlador.Login;
import ec.edu.monster.service.EurekaService;
import ec.edu.monster.ws.Movimiento;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.XMLGregorianCalendar;

public class WSEurekaClienteConsola {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Pedir usuario y contraseña
        System.out.print("EUREKABANK \n");
        System.out.print("Ingrese el usuario: ");
        String usuario = scanner.nextLine();
        System.out.print("Ingrese la contraseña: ");
        String contrasena = scanner.nextLine();

        // Proceso de login
        Login loginService = new Login();
        if (loginService.iniciarSesion(usuario, contrasena)) {
            System.out.println("¡Bienvenido!");
            mostrarMenu(scanner);
        } else {
            System.out.println("Login fallido");
        }
    }

    private static void mostrarMenu(Scanner scanner) {
        int opcion = -1;
        while (opcion != 3) {
            System.out.println("Menú de Servicios Eurekabank");
            System.out.println("1. Depósitos");
            System.out.println("2. Movimientos");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1:
                    realizarDeposito(scanner);
                    break;
                case 2:
                    consultarMovimientos(scanner);
                    break;
                case 3:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        }
    }

    private static void consultarMovimientos(Scanner scanner) {
        System.out.print("Ingrese el número de cuenta: ");
        String cuenta = scanner.nextLine();

        List<Movimiento> movimientos = EurekaService.traerMovimientos(cuenta);
        if (movimientos.isEmpty()) {
            System.out.println("No se encontraron movimientos para la cuenta: " + cuenta);
        } else {
            // Mostrar los títulos de las columnas con más espacio
            System.out.println(String.format("%-15s %-15s %-15s %-15s %-15s %-15s",
                                             "CUENTA N°", "MOVIMIENTO", "FECHA", "TIPO", "ACCIÓN", "IMPORTE"));

            // Crear un formato para la fecha
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Movimiento mov : movimientos) {
                try {
                    // Convertir el XMLGregorianCalendar a Date
                    XMLGregorianCalendar xmlDate = mov.getFecha();
                    Date fechaDate = xmlDate.toGregorianCalendar().getTime();

                    // Formatear la fecha
                    String fechaFormateada = outputDateFormat.format(fechaDate);

                    // Mostrar la información del movimiento con más separación
                    System.out.println(String.format("%-15s %-15d %-15s %-15s %-15s %-15.2f",
                                                     mov.getCuenta(), mov.getNromov(), fechaFormateada,
                                                     mov.getTipo(), mov.getAccion(), mov.getImporte()));
                } catch (Exception e) {
                    System.out.println("Error al convertir la fecha: " + mov.getFecha());
                }
            }
        }
    }

    private static void realizarDeposito(Scanner scanner) {
        System.out.print("Realizar Depósito\n");
        System.out.print("Número de cuenta: ");
        String cuenta = scanner.nextLine();
        System.out.print("Monto del Depósito: ");
        double importe = scanner.nextDouble();
        System.out.print("Número de cuenta: ");
        String codigo = scanner.nextLine();
        
        int resultado = EurekaService.regDeposito(cuenta, importe);
        if (resultado == 1) {
            System.out.println("Depósito realizado con éxito en la cuenta: " + cuenta);
        } else {
            System.out.println("Error al realizar el depósito");
        }
    }
}
