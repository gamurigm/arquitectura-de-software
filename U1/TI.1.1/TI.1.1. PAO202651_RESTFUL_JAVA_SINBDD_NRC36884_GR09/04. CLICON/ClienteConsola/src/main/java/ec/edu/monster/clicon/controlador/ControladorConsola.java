package ec.edu.monster.clicon.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import ec.edu.monster.clicon.modelo.SolicitudConversion;
import ec.edu.monster.clicon.servicios.ApiClient;
import ec.edu.monster.clicon.servicios.ApiException;
import ec.edu.monster.clicon.vista.ConsolaVista;

import java.util.Scanner;

/**
 * Cliente de consola para el sistema "Conversion de Unidades Monster".
 * Consume la API REST del servidor central.
 */
public class ControladorConsola {

    public void iniciar() {
        Scanner sc = new Scanner(System.in);
        ConsolaVista.mostrar("=====================================================");
        ConsolaVista.mostrar(" Cliente de Consola - Conversion de Unidades Monster");
        ConsolaVista.mostrar("=====================================================");

        ConsolaVista.escribir("URL del servidor [http://localhost:8080]: ");
        String base = ConsolaVista.leerLinea(sc).trim();
        if (base.isEmpty()) {
            base = "http://localhost:8080";
        }

        ApiClient api = new ApiClient(base);
        String token = autenticar(sc, api);
        if (token == null) {
            ConsolaVista.mostrar("No se pudo iniciar sesion. Fin del programa.");
            return;
        }

        boolean salir = false;
        while (!salir) {
            ConsolaVista.mostrar("\n--- Menu principal ---");
            ConsolaVista.mostrar("1) Convertir Longitud");
            ConsolaVista.mostrar("2) Convertir Masa");
            ConsolaVista.mostrar("3) Convertir Temperatura");
            ConsolaVista.mostrar("4) Ver catalogo de unidades");
            ConsolaVista.mostrar("0) Salir");
            ConsolaVista.escribir("Opcion: ");
            String opcion = ConsolaVista.leerLinea(sc).trim();
            try {
                switch (opcion) {
                    case "1" -> convertir(sc, api, "longitud", token);
                    case "2" -> convertir(sc, api, "masa", token);
                    case "3" -> convertir(sc, api, "temperatura", token);
                    case "4" -> mostrarCatalogo(api);
                    case "0" -> salir = true;
                    default -> ConsolaVista.mostrar("Opcion no valida.");
                }
            } catch (ApiException e) {
                ConsolaVista.mostrar("Error del servidor (" + e.getStatus() + "): " + e.getMessage());
                if (e.esNoAutorizado()) {
                    token = autenticar(sc, api);
                    if (token == null) {
                        salir = true;
                    }
                }
            } catch (Exception e) {
                ConsolaVista.mostrar("Error de conexion: " + e.getMessage());
            }
        }
        ConsolaVista.mostrar("Hasta luego.");
    }

    private static String autenticar(Scanner sc, ApiClient api) {
        ConsolaVista.mostrar("\n--- Inicio de sesion ---");
        ConsolaVista.escribir("Usuario [monster]: ");
        String usuario = ConsolaVista.leerLinea(sc).trim();
        if (usuario.isEmpty()) {
            usuario = "monster";
        }
        String clave = ConsolaVista.leerClave();
        if (clave.isEmpty()) {
            clave = "monster9";
        }
        try {
            String token = api.login(usuario, clave);
            ConsolaVista.mostrar("Sesion iniciada.");
            return token;
        } catch (Exception e) {
            ConsolaVista.mostrar("Fallo el login: " + e.getMessage());
            return null;
        }
    }

    private static void convertir(Scanner sc, ApiClient api, String magnitud, String token) throws Exception {
        ConsolaVista.mostrar("\n-- Conversion de " + magnitud + " --");
        ConsolaVista.escribir("Valor a convertir: ");
        double valor = Double.parseDouble(ConsolaVista.leerLinea(sc).trim().replace(",", "."));
        ConsolaVista.escribir("Unidad de origen: ");
        String origen = ConsolaVista.leerLinea(sc).trim();
        ConsolaVista.escribir("Unidad de destino: ");
        String destino = ConsolaVista.leerLinea(sc).trim();

        SolicitudConversion solicitud = new SolicitudConversion(valor, origen, destino);
        JsonNode respuesta = api.convertir(magnitud, solicitud.valor(), solicitud.origen(), solicitud.destino(), token);
        ConsolaVista.mostrar(">> " + respuesta.path("mensaje").asText()
                + "  (resultado=" + respuesta.path("resultado").asDouble() + ")");
    }

    private static void mostrarCatalogo(ApiClient api) throws Exception {
        JsonNode respuesta = api.catalogo();
        JsonNode magnitudes = respuesta.path("magnitudes");
        ConsolaVista.mostrar("\n-- Catalogo de unidades --");
        magnitudes.fields().forEachRemaining(entrada ->
                ConsolaVista.mostrar("  " + entrada.getKey() + ": " + entrada.getValue()));
    }
}
