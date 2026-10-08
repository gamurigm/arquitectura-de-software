package ec.edu.monster.pruebas;

import ec.edu.monster.modelos.utilidades.ConversorMasa;
import ec.edu.monster.modelos.utilidades.enums.UnidadMasa;

import java.util.Scanner;

public class ConversorMasaPrueba {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ConversorMasa conversor = new ConversorMasa();

        System.out.print("Ingrese valor: ");
        double valor = sc.nextDouble();

        System.out.print("Unidad origen: ");
        String origen = sc.next();

        System.out.print("Unidad destino: ");
        String destino = sc.next();

        try {
            double resultado = conversor.convertir(
                    valor,
                    UnidadMasa.valueOf(origen.toUpperCase()),
                    UnidadMasa.valueOf(destino.toUpperCase())
            );

            System.out.println("Resultado: " + resultado);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}