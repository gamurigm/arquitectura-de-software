package ec.edu.monster.servicios;

import ec.edu.monster.BuildConfig;
import ec.edu.monster.modelo.Movimiento;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ClienteEurekaRest {
    private final String baseUrl;

    public ClienteEurekaRest() {
        URI uri = URI.create(BuildConfig.API_BASE_URL);
        if (uri.getHost() == null || !("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) {
            throw new IllegalArgumentException("La URL del servidor debe usar HTTP o HTTPS.");
        }
        baseUrl = BuildConfig.API_BASE_URL.replaceAll("/+$", "");
    }

    public String iniciarSesion(String usuario, String contrasena) throws IOException {
        JSONObject respuesta = enviar("POST", "/login", objeto("usuario", usuario, "contrasena", contrasena));
        return respuesta.optString("token", null);
    }

    public List<Movimiento> traerMovimientos(String cuenta) throws IOException {
        String ruta = "/movimientos?cuenta=" + URLEncoder.encode(cuenta, StandardCharsets.UTF_8);
        HttpURLConnection conexion = abrir("GET", ruta, null);
        try {
            String cuerpo = leerRespuesta(conexion);
            JSONArray filas = new JSONArray(cuerpo);
            List<Movimiento> movimientos = new ArrayList<>();
            for (int i = 0; i < filas.length(); i++) {
                JSONObject fila = filas.getJSONObject(i);
                movimientos.add(new Movimiento(fila.optInt("numero"), fila.optString("cuenta"),
                        fila.optString("fecha"), fila.optString("tipo"), fila.optString("accion"),
                        new BigDecimal(fila.get("importe").toString())));
            }
            return movimientos;
        } catch (org.json.JSONException | NumberFormatException e) {
            throw new IOException("El servidor envió movimientos con formato inválido.", e);
        } finally { conexion.disconnect(); }
    }

    public boolean registrarDeposito(String cuenta, BigDecimal importe) throws IOException {
        JSONObject solicitud = objeto("cuenta", cuenta, "importe", importe);
        return enviar("POST", "/depositos", solicitud).optBoolean("registrado", false);
    }

    public boolean transferir(String token, String origen, String destino, BigDecimal importe, String claveOperacion) throws IOException {
        JSONObject solicitud = objeto("cuentaOrigen", origen, "cuentaDestino", destino, "importe", importe,
                "claveOperacion", claveOperacion);
        return enviar(token, "POST", "/transferencias", solicitud).optBoolean("registrada", false);
    }

    public void cerrarSesion(String token) throws IOException {
        HttpURLConnection conexion = abrir("POST", "/logout", new JSONObject(), token);
        try {
            int estado = conexion.getResponseCode();
            if (estado < 200 || estado >= 300) throw new IOException("El servidor respondió HTTP " + estado + ".");
        } finally { conexion.disconnect(); }
    }

    private static JSONObject objeto(String campo1, Object valor1, String campo2, Object valor2) throws IOException {
        try { return new JSONObject().put(campo1, valor1).put(campo2, valor2); }
        catch (org.json.JSONException e) { throw new IOException("No se pudo preparar la solicitud.", e); }
    }

    private static JSONObject objeto(String c1, Object v1, String c2, Object v2, String c3, Object v3, String c4, Object v4) throws IOException {
        try { return new JSONObject().put(c1, v1).put(c2, v2).put(c3, v3).put(c4, v4); }
        catch (org.json.JSONException e) { throw new IOException("No se pudo preparar la transferencia.", e); }
    }

    private JSONObject enviar(String metodo, String ruta, JSONObject cuerpo) throws IOException {
        return enviar(null, metodo, ruta, cuerpo);
    }

    private JSONObject enviar(String token, String metodo, String ruta, JSONObject cuerpo) throws IOException {
        HttpURLConnection conexion = abrir(metodo, ruta, cuerpo, token);
        try { return new JSONObject(leerRespuesta(conexion)); }
        catch (org.json.JSONException e) { throw new IOException("El servidor envió una respuesta inválida.", e); }
        finally { conexion.disconnect(); }
    }

    private HttpURLConnection abrir(String metodo, String ruta, JSONObject cuerpo) throws IOException {
        return abrir(metodo, ruta, cuerpo, null);
    }

    private HttpURLConnection abrir(String metodo, String ruta, JSONObject cuerpo, String token) throws IOException {
        URL url = URI.create(baseUrl + ruta).toURL();
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod(metodo);
        conexion.setConnectTimeout(3000);
        conexion.setReadTimeout(8000);
        conexion.setRequestProperty("Accept", "application/json");
        if (token != null && !token.isBlank()) conexion.setRequestProperty("Authorization", "Bearer " + token);
        if (cuerpo != null) {
            conexion.setDoOutput(true);
            conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            try (var salida = conexion.getOutputStream()) { salida.write(cuerpo.toString().getBytes(StandardCharsets.UTF_8)); }
            catch (IOException e) { conexion.disconnect(); throw e; }
        }
        return conexion;
    }

    private static String leerRespuesta(HttpURLConnection conexion) throws IOException {
        int estado = conexion.getResponseCode();
        InputStream flujo = estado >= 200 && estado < 300 ? conexion.getInputStream() : conexion.getErrorStream();
        if (flujo == null) throw new IOException("El servidor respondió HTTP " + estado + ".");
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
            StringBuilder texto = new StringBuilder();
            String linea;
            while ((linea = lector.readLine()) != null) texto.append(linea);
            if (estado < 200 || estado >= 300) throw new IOException("El servidor respondió HTTP " + estado + ".");
            return texto.toString();
        }
    }
}
