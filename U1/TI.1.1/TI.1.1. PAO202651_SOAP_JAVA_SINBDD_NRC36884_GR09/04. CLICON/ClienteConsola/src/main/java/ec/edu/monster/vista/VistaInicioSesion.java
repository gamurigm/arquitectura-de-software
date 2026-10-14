package ec.edu.monster.vista;

import ec.edu.monster.controlador.ConversionControlador;
import ec.edu.monster.controlador.ControladorInicioSesion;
import ec.edu.monster.modelo.ModeloInicioSesion;
import ec.edu.monster.util.EntradaUtil;
import jakarta.xml.ws.soap.SOAPFaultException;

public class VistaInicioSesion {

    private final MensajesVista mensajes = new MensajesVista();
    private ControladorInicioSesion loginControlador;

    public void iniciar() {
        this.mensajeBienvenida();
        mensajes.mostrarTitulo("=== CONVERSOR DE UNIDADES - GR 9 ===");

        try {
            this.loginControlador = new ControladorInicioSesion();
            mensajes.mostrarMensaje("Servidor SOAP detectado: " + loginControlador.obtenerDireccionServicioActiva());
        } catch (Exception ex) {
            mensajes.mostrarError(ex.getMessage());
            mensajes.mostrarMensaje(
                    "Si el endpoint cambia, configure WS_BASE_URL o la propiedad -Dws.base.url con su URL base."
            );
            return;
        }

        while (true) {
            String usuario = EntradaUtil.leerTextoConPredeterminado("Usuario [Monster]: ", "Monster");
            String contrasenia = EntradaUtil.leerContrasenia("Contrasenia [Monster9]: ", "Monster9");

            ModeloInicioSesion modeloInicioSesion = new ModeloInicioSesion(usuario, contrasenia);

            try {
                String token = loginControlador.autenticar(modeloInicioSesion);
                mensajes.mostrarMensaje("Inicio de sesion exitoso. Token: " + token);

                ConversionControlador controladorConversion = loginControlador.crearControladorConversion();
                new ConversionVista(controladorConversion, loginControlador).ejecutarMenu();

                loginControlador.cerrarSesion();
                mensajes.mostrarMensaje("Sesion finalizada.");
                return;
            } catch (SOAPFaultException ex) {
                mensajes.mostrarError(ex.getFault().getFaultString());
            } catch (Exception ex) {
                mensajes.mostrarError(ex.getMessage());
            }
        }
    }
    public void mensajeBienvenida() {
        // Códigos ANSI para darle color en la consola
        // Bloque de texto (Java 15+) con las barras invertidas ya escapadas (\\)
        String mensaje = """
        ╔────────────────────────────────────────────────────────────╗
        │    ____            _   _       _     ____  ____  ____      │
        │   / ___|___  _ __ | | | |_ __ (_)   / ___||  _ \\| ___|     │
        │  | |   / _ \\| '_ \\| | | | '_ \\| |  | |  _ | |_) |___ \\     │
        │  | |__| (_) | | | | |_| | | | | |  | |_| ||  _ < ___) |    │
        │   \\____\\___/|_| |_|\\___/|_| |_|_|   \\____||_| \\_\\____/     │
        ╚────────────────────────────────────────────────────────────╝""";

        // Imprimir con el color aplicado y luego restaurar al color normal
        mensajes.mostrarBanner(mensaje);
    }
}

