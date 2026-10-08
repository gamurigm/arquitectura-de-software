package ec.edu.monster.pruebas;

import ec.edu.monster.modelos.utilidades.ConversorTemperatura;
import ec.edu.monster.modelos.utilidades.enums.UnidadTemperatura;

import java.util.Scanner;

public class ConversorTemperaturaPrueba {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ConversorTemperatura conversor = new ConversorTemperatura();

        System.out.print("Ingrese valor: ");
        double valor = sc.nextDouble();

        System.out.print("Unidad origen: ");
        String origen = sc.next();

        System.out.print("Unidad destino: ");
        String destino = sc.next();

        try {
            double resultado = conversor.convertir(
                    valor,
                    UnidadTemperatura.valueOf(origen.toUpperCase()),
                    UnidadTemperatura.valueOf(destino.toUpperCase())
            );

            System.out.println("Resultado: " + resultado);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}