package ec.edu.monster.clicon;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Scanner;

/**
 * Cliente de consola para el sistema "Conversion de Unidades Monster".
 * Consume la API REST del servidor central.
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=====================================================");
        System.out.println(" Cliente de Consola - Conversion de Unidades Monster");
        System.out.println("=====================================================");

        System.out.print("URL del servidor [http://localhost:8080]: ");
        String base = sc.nextLine().trim();
        if (base.isEmpty()) {
            base = "http://localhost:8080";
        }

        ApiClient api = new ApiClient(base);
        String token = autenticar(sc, api);
        if (token == null) {
            System.out.println("No se pudo iniciar sesion. Fin del programa.");
            return;
        }

        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- Menu principal ---");
            System.out.println("1) Convertir Longitud");
            System.out.println("2) Convertir Masa");
            System.out.println("3) Convertir Temperatura");
            System.out.println("4) Ver catalogo de unidades");
            System.out.println("0) Salir");
            System.out.print("Opcion: ");
            String opcion = sc.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> convertir(sc, api, "longitud", token);
                    case "2" -> convertir(sc, api, "masa", token);
                    case "3" -> convertir(sc, api, "temperatura", token);
                    case "4" -> mostrarCatalogo(api);
                    case "0" -> salir = true;
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (ApiException e) {
                System.out.println("Error del servidor (" + e.getStatus() + "): " + e.getMessage());
                if (e.esNoAutorizado()) {
                    token = autenticar(sc, api);
                    if (token == null) {
                        salir = true;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error de conexion: " + e.getMessage());
            }
        }
        System.out.println("Hasta luego.");
    }

    private static String autenticar(Scanner sc, ApiClient api) {
        System.out.println("\n--- Inicio de sesion ---");
        System.out.print("Usuario [monster]: ");
        String usuario = sc.nextLine().trim();
        if (usuario.isEmpty()) {
            usuario = "monster";
        }
        System.out.print("Clave [monster9]: ");
        String clave = sc.nextLine().trim();
        if (clave.isEmpty()) {
            clave = "monster9";
        }
        try {
            String token = api.login(usuario, clave);
            System.out.println("Sesion iniciada. Token: " + token);
            return token;
        } catch (Exception e) {
            System.out.println("Fallo el login: " + e.getMessage());
            return null;
        }
    }

    private static void convertir(Scanner sc, ApiClient api, String magnitud, String token) throws Exception {
        System.out.println("\n-- Conversion de " + magnitud + " --");
        System.out.print("Valor a convertir: ");
        double valor = Double.parseDouble(sc.nextLine().trim().replace(",", "."));
        System.out.print("Unidad de origen: ");
        String origen = sc.nextLine().trim();
        System.out.print("Unidad de destino: ");
        String destino = sc.nextLine().trim();

        JsonNode respuesta = api.convertir(magnitud, valor, origen, destino, token);
        System.out.println(">> " + respuesta.path("mensaje").asText()
                + "  (resultado=" + respuesta.path("resultado").asDouble() + ")");
    }

    private static void mostrarCatalogo(ApiClient api) throws Exception {
        JsonNode respuesta = api.catalogo();
        JsonNode magnitudes = respuesta.path("magnitudes");
        System.out.println("\n-- Catalogo de unidades --");
        magnitudes.fields().forEachRemaining(entrada ->
                System.out.println("  " + entrada.getKey() + ": " + entrada.getValue()));
    }
}
