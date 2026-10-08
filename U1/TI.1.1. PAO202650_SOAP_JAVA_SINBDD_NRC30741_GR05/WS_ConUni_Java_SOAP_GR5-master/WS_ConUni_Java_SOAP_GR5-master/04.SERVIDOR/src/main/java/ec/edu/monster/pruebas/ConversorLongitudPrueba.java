package ec.edu.monster.pruebas;

import ec.edu.monster.modelos.utilidades.ConversorLongitud;
import ec.edu.monster.modelos.utilidades.enums.UnidadLongitud;

import java.util.Scanner;

public class ConversorLongitudPrueba {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ConversorLongitud conversor = new ConversorLongitud();

        System.out.print("Ingrese valor: ");
        double valor = sc.nextDouble();

        System.out.print("Unidad origen: ");
        String origen = sc.next();

        System.out.print("Unidad destino: ");
        String destino = sc.next();

        try {
            double resultado = conversor.convertir(
                    valor,
                    UnidadLongitud.valueOf(origen.toUpperCase()),
                    UnidadLongitud.valueOf(destino.toUpperCase())
            );

            System.out.println("Resultado: " + resultado);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}