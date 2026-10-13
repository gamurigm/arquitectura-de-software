package ec.edu.monster.clienteescritorio.controlador;

import ec.edu.monster.clienteescritorio.modelo.ConversionModelo;
import ec.edu.monster.clienteescritorio.modelo.SesionModelo;
import ec.edu.monster.clienteescritorio.servicios.ClienteSOAP;
import ec.edu.monster.clienteescritorio.vista.ConversionVista;
import ec.edu.monster.clienteescritorio.vista.LoginVista;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public final class ControladorPrincipal {

    private final SesionModelo sesion = new SesionModelo();
    private LoginVista vistaLogin;
    private ConversionVista vistaConversion;
    private ClienteSOAP clienteSOAP;

    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(() -> new ControladorPrincipal().mostrarLogin());
    }

    private void mostrarLogin() {
        vistaLogin = new LoginVista();
        vistaLogin.alIngresar(evento -> iniciarSesion());
        vistaLogin.setVisible(true);
    }

    private void iniciarSesion() {
        String usuario = vistaLogin.obtenerUsuario();
        String contrasenia = vistaLogin.obtenerContrasenia();
        if (usuario.isBlank() || contrasenia.isBlank()) {
            JOptionPane.showMessageDialog(vistaLogin,
                    "Ingrese usuario y contraseña.", "Datos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        vistaLogin.establecerCargando(true);
        new SwingWorker<ClienteSOAP, Void>() {
            @Override
            protected ClienteSOAP doInBackground() {
                ClienteSOAP cliente = new ClienteSOAP();
                cliente.iniciarSesion(usuario, contrasenia);
                return cliente;
            }

            @Override
            protected void done() {
                try {
                    clienteSOAP = get();
                    sesion.iniciar(usuario, clienteSOAP.obtenerToken());
                    vistaLogin.dispose();
                    mostrarConversiones();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    vistaLogin.establecerCargando(false);
                    mostrarErrorLogin("Se interrumpió la conexión con el servidor.");
                } catch (ExecutionException ex) {
                    vistaLogin.establecerCargando(false);
                    mostrarErrorLogin(mensajeError(ex.getCause()));
                }
            }
        }.execute();
    }

    private void mostrarConversiones() {
        vistaConversion = new ConversionVista(sesion.obtenerUsuario());
        vistaConversion.alConvertir(evento -> convertir());
        vistaConversion.alCerrarSesion(this::cerrarSesion);
        vistaConversion.setVisible(true);
    }

    private void convertir() {
        final ConversionModelo conversion;
        try {
            conversion = vistaConversion.obtenerConversion();
        } catch (IllegalArgumentException ex) {
            vistaConversion.mostrarError(ex.getMessage());
            return;
        }

        if (!sesion.estaVigente()) {
            volverAlLogin("La sesión venció. Inicie sesión nuevamente.");
            return;
        }

        vistaConversion.establecerCargando(true);
        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() {
                return clienteSOAP.convertir(conversion.tipo().obtenerValorServicio(), conversion.valor(),
                        conversion.unidadInicial(), conversion.unidadFinal());
            }

            @Override
            protected void done() {
                try {
                    vistaConversion.mostrarResultado(get());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    vistaConversion.mostrarError("Se interrumpió la conversión.");
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    if (tokenVencido(causa)) {
                        volverAlLogin("La sesión venció. Inicie sesión nuevamente.");
                    } else {
                        vistaConversion.mostrarError(mensajeError(causa));
                    }
                } finally {
                    if (vistaConversion.isDisplayable()) {
                        vistaConversion.establecerCargando(false);
                    }
                }
            }
        }.execute();
    }

    private void cerrarSesion() {
        if (clienteSOAP != null) {
            clienteSOAP.cerrarSesion();
        }
        sesion.cerrar();
        vistaConversion.dispose();
        mostrarLogin();
    }

    private void volverAlLogin(String mensaje) {
        cerrarSesion();
        JOptionPane.showMessageDialog(vistaLogin, mensaje, "Sesión finalizada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarErrorLogin(String mensaje) {
        JOptionPane.showMessageDialog(vistaLogin, mensaje, "No se pudo iniciar sesión", JOptionPane.ERROR_MESSAGE);
    }

    private static boolean tokenVencido(Throwable error) {
        for (Throwable causa = error; causa != null; causa = causa.getCause()) {
            String mensaje = causa.getMessage();
            if (mensaje != null && (mensaje.toLowerCase().contains("token inválido")
                    || mensaje.toLowerCase().contains("token invalido")
                    || mensaje.toLowerCase().contains("no se encontró el token"))) {
                return true;
            }
        }
        return false;
    }

    private static String mensajeError(Throwable error) {
        for (Throwable causa = error; causa != null; causa = causa.getCause()) {
            String mensaje = causa.getMessage();
            if (mensaje != null && mensaje.toLowerCase().contains("credenciales incorrectas")) {
                return "Usuario o contraseña incorrectos.";
            }
        }
        if (error == null || error.getMessage() == null || error.getMessage().isBlank()) {
            return "No se pudo completar la operación con el servidor SOAP.";
        }
        return error.getMessage();
    }
}
