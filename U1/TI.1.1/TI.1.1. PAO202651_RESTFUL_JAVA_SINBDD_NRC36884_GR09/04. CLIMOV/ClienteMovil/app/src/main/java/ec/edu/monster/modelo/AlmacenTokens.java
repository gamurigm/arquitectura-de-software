package ec.edu.monster.modelo;

import android.content.Context;
import android.content.SharedPreferences;

/** Persistencia local de la sesión (URL base, usuario y token). */
public class AlmacenTokens {

    private static final String PREFS = "monster_sesion";
    private static final String K_BASE = "urlBase";
    private static final String K_USUARIO = "usuario";
    private static final String K_TOKEN = "token";

    private final SharedPreferences prefs;

    public AlmacenTokens(Context contexto) {
        this.prefs = contexto.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void guardarSesion(String urlBase, String usuario, String token) {
        prefs.edit()
                .putString(K_BASE, urlBase)
                .putString(K_USUARIO, usuario)
                .putString(K_TOKEN, token)
                .apply();
    }

    public String urlBase() {
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
