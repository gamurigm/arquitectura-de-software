package ec.edu.monster.climov.modelo;

import android.content.Context;
import android.content.SharedPreferences;

/** Persistencia local de la sesion (base URL, usuario y token). */
public class TokenStore {

    private static final String PREFS = "monster_sesion";
    private static final String K_BASE = "baseUrl";
    private static final String K_USUARIO = "usuario";
    private static final String K_TOKEN = "token";

    private final SharedPreferences prefs;

    public TokenStore(Context contexto) {
        this.prefs = contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void guardarSesion(String baseUrl, String usuario, String token) {
        prefs.edit()
                .putString(K_BASE, baseUrl)
                .putString(K_USUARIO, usuario)
                .putString(K_TOKEN, token)
                .apply();
    }

    public String baseUrl() {
        return prefs.getString(K_BASE, "http://10.0.2.2:8080");
    }

    public String usuario() {
        return prefs.getString(K_USUARIO, "");
    }

    public String token() {
        return prefs.getString(K_TOKEN, null);
    }

    public String bearer() {
        String token = token();
        return token == null ? "" : "Bearer " + token;
    }

    public void limpiar() {
        prefs.edit().clear().apply();
    }
}
