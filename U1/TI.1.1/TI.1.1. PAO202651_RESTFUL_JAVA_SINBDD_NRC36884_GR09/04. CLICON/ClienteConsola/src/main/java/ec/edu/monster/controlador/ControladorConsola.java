package ec.edu.monster.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import ec.edu.monster.modelo.SolicitudConversion;
import ec.edu.monster.servicios.ClienteApi;
import ec.edu.monster.servicios.ExcepcionApi;
import ec.edu.monster.vista.ConsolaVista;

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
        String urlBase = ConsolaVista.leerLinea(sc).trim();
        if (urlBase.isEmpty()) {
            urlBase = "http://localhost:8080";
        }

        ClienteApi api;
        while (true) {
            try {
                api = new ClienteApi(urlBase);
                break;
            } catch (IllegalArgumentException ex) {
                ConsolaVista.mostrar(ex.getMessage());
                ConsolaVista.escribir("URL del servidor [http://localhost:8080]: ");
                urlBase = ConsolaVista.leerLinea(sc).trim();
                if (urlBase.isEmpty()) urlBase = "http://localhost:8080";
            }
        }
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
            } catch (ExcepcionApi e) {
                ConsolaVista.mostrar("Error del servidor (" + e.obtenerEstadoHttp() + "): " + e.getMessage());
                if (e.esNoAutorizado()) {
                    token = autenticar(sc, api);
                    if (token == null) {
                        salir = true;
                    }
                }
            } catch (Exception e) {
                ConsolaVista.mostrar("Error de conexión: " + e.getMessage());
            }
        }
        ConsolaVista.mostrar("Hasta luego.");
    }

    private static String autenticar(Scanner sc, ClienteApi api) {
        ConsolaVista.mostrar("\n--- Inicio de sesion ---");
        ConsolaVista.escribir("Usuario [Monster]: ");
        String usuario = ConsolaVista.leerLinea(sc).trim();
        if (usuario.isEmpty()) {
            usuario = "Monster";
        }
        String clave = ConsolaVista.leerClave();
        if (clave.isEmpty()) {
            clave = "Monster9";
        }
        try {
            String token = api.iniciarSesion(usuario, clave);
            ConsolaVista.mostrar("Sesion iniciada.");
            return token;
        } catch (Exception e) {
            ConsolaVista.mostrar("Falló el inicio de sesión: " + e.getMessage());
            return null;
        }
    }

    private static void convertir(Scanner sc, ClienteApi api, String magnitud, String token) throws Exception {
        ConsolaVista.mostrar("\n-- Conversion de " + magnitud + " --");
        double valor;
        while (true) {
            ConsolaVista.escribir("Valor a convertir: ");
            try {
                valor = Double.parseDouble(ConsolaVista.leerLinea(sc).trim().replace(",", "."));
                if (!Double.isFinite(valor) || ("masa".equals(magnitud) && valor < 0)) {
                    throw new NumberFormatException();
                }
                break;
            } catch (NumberFormatException ex) {
                ConsolaVista.mostrar("Ingrese un número finito; la masa no puede ser negativa.");
            }
        }
        JsonNode disponibles = api.obtenerCatalogo().path("magnitudes").path(magnitud);
        java.util.List<String> unidades = new java.util.ArrayList<>();
        disponibles.forEach(unidad -> unidades.add(unidad.asText()));
        if (unidades.isEmpty()) {
            throw new IllegalStateException("El catálogo no tiene unidades disponibles para " + magnitud + ".");
        }
        String origen = leerUnidad(sc, "Unidad de origen", unidades);
        String destino = leerUnidad(sc, "Unidad de destino", unidades);

        SolicitudConversion solicitud = new SolicitudConversion(valor, origen, destino);
        JsonNode respuesta = api.convertir(magnitud, solicitud.valor(), solicitud.origen(), solicitud.destino(), token);
        double resultado = respuesta.path("resultado").asDouble(Double.NaN);
        if (!Double.isFinite(resultado)) {
            throw new IllegalArgumentException("El servidor devolvió un resultado inválido.");
        }
        ConsolaVista.mostrar(">> " + respuesta.path("mensaje").asText()
                + "  (resultado=" + resultado + ")");
    }

    private static String leerUnidad(Scanner sc, String etiqueta, java.util.List<String> unidades) {
        for (int i = 0; i < unidades.size(); i++) {
            ConsolaVista.mostrar((i + 1) + ") " + unidades.get(i));
        }
        while (true) {
            ConsolaVista.escribir(etiqueta + " (número): ");
            try {
                int indice = Integer.parseInt(ConsolaVista.leerLinea(sc).trim());
                if (indice >= 1 && indice <= unidades.size()) return unidades.get(indice - 1);
            } catch (NumberFormatException ignored) {
            }
            ConsolaVista.mostrar("Seleccione una unidad de la lista.");
        }
    }

    private static void mostrarCatalogo(ClienteApi api) throws Exception {
        JsonNode respuesta = api.obtenerCatalogo();
        JsonNode magnitudes = respuesta.path("magnitudes");
        ConsolaVista.mostrar("\n-- Catalogo de unidades --");
        magnitudes.properties().forEach(entrada ->
                ConsolaVista.mostrar("  " + entrada.getKey() + ": " + entrada.getValue()));
    }
}
