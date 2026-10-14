package ec.edu.monster.controlador;

import ec.edu.monster.servicios.soap.ClienteWSConversor;
import ec.edu.monster.modelo.ModeloInicioSesion;

public class ControladorInicioSesion {

    private final ClienteWSConversor clienteWs;

    public ControladorInicioSesion() {
        this.clienteWs = new ClienteWSConversor();
    }

    public String autenticar(ModeloInicioSesion modeloInicioSesion) {
        return clienteWs.iniciarSesion(modeloInicioSesion.obtenerUsuario(), modeloInicioSesion.obtenerContrasenia());
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

