package ec.edu.monster.conuniclientemovil.modelo;

public final class Sesion {

    public static final String CLAVE_TOKEN = "ec.edu.monster.conuniclientemovil.TOKEN";

    private final String token;

    public Sesion(String token) {
        this.token = token;
    }

    public String obtenerToken() {
        return token;
    }

    public boolean estaActiva() {
        return token != null && !token.isBlank();
    }
}
