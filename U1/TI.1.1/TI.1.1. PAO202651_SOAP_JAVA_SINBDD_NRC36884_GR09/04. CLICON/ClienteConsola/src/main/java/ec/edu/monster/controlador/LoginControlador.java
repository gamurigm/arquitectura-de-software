package ec.edu.monster.controlador;

import ec.edu.monster.cliente.soap.ClienteWSConversor;
import ec.edu.monster.modelo.LoginModelo;

public class LoginControlador {

    private final ClienteWSConversor clienteWs;

    public LoginControlador() {
        this.clienteWs = new ClienteWSConversor();
    }

    public String autenticar(LoginModelo loginModelo) {
        return clienteWs.login(loginModelo.obtenerUsuario(), loginModelo.obtenerContrasenia());
    }

    public String cambiarContrasenia(String contraseniaActual, String contraseniaNueva) {
        return clienteWs.cambiarContrasenia(contraseniaActual, contraseniaNueva);
    }

    public ConversionControlador crearControladorConversion() {
        return new ConversionControlador(clienteWs);
    }

    public String obtenerDireccionServicioActiva() {
        return clienteWs.obtenerDireccionServicioActiva();
    }

    public void cerrarSesion() {
        clienteWs.cerrarSesion();
    }
}

