package ec.edu.monster.modelo;

public class LoginModelo {

    private String usuario;
    private String contrasenia;

    public LoginModelo() {
    }

    public LoginModelo(String usuario, String contrasenia) {
        this.usuario = usuario;
        this.contrasenia = contrasenia;
    }

    public String obtenerUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String obtenerContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}

