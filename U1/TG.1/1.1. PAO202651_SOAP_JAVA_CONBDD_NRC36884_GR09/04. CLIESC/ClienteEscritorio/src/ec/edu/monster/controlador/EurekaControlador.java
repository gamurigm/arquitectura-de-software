package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.service.EurekaService;
import ec.edu.monster.vista.DepositosVista;
import ec.edu.monster.vista.LoginVista;
import ec.edu.monster.vista.MenuVista;
import ec.edu.monster.vista.MovimientosVista;
import ec.edu.monster.vista.TransferenciasVista;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public final class EurekaControlador {
    private final EurekaService servicio;
    private boolean autenticado;
    private String token;
    private TransferenciaPendiente transferenciaPendiente;

    public EurekaControlador(EurekaService servicio) {
        this.servicio = servicio;
    }

    public void iniciar() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::iniciar);
            return;
        }
        LoginVista login = new LoginVista();
        login.alEntrar(() -> {
            String usuario = login.usuario();
            char[] contrasena = login.contrasena();
            login.limpiarContrasena();
            ejecutar(login::ocupada, login::mostrarMensaje,
                    () -> iniciarSesion(usuario, contrasena),
                    aceptado -> {
                        if (aceptado) {
                            login.dispose();
                            abrirMenu(usuario.strip(), login);
                        } else {
                            login.mostrarMensaje("Usuario o contraseña incorrectos.");
                        }
                    }, "No se pudo conectar con EurekaBank. Intente iniciar sesión nuevamente.");
        });
        login.setVisible(true);
    }

    private void abrirMenu(String usuario, LoginVista login) {
        MenuVista menu = new MenuVista(usuario);
        menu.alConsultar(() -> {
            MovimientosVista vista = new MovimientosVista(menu);
            vista.alBuscar(() -> {
                String cuenta = vista.cuenta();
                vista.limpiar();
                ejecutar(vista::ocupada, vista::mostrarMensaje,
                        () -> consultarMovimientos(cuenta), vista::mostrarMovimientos,
                        "No se pudieron consultar los movimientos. Revise la conexión.");
            });
            vista.setVisible(true);
        });
        menu.alDepositar(() -> {
            DepositosVista vista = new DepositosVista(menu);
            vista.alDepositar(() -> {
                String cuenta = vista.cuenta();
                String importe = vista.importe();
                ejecutar(vista::ocupada, vista::mostrarMensaje,
                        () -> registrarDeposito(cuenta, importe),
                        registrado -> {
                            if (registrado) {
                                vista.limpiarImporte();
                                vista.mostrarMensaje("Depósito registrado.");
                            } else {
                                vista.mostrarMensaje("Depósito rechazado. Consulte movimientos antes de repetirlo.");
                            }
                        }, "No se pudo confirmar el depósito. Consulte movimientos antes de repetirlo.");
            });
            vista.setVisible(true);
        });
        menu.alTransferir(() -> {
            TransferenciasVista vista = new TransferenciasVista(menu);
            vista.alTransferir(() -> {
                String origen = vista.origen(), destino = vista.destino(), importe = vista.importe();
                ejecutar(vista::ocupada, vista::mostrarMensaje,
                        () -> transferir(origen, destino, importe), registrada -> {
                            if (registrada) { vista.limpiar(); vista.mostrarMensaje("Transferencia registrada: débito y crédito confirmados."); }
                            else vista.mostrarMensaje("Transferencia rechazada. Revise cuentas, moneda, saldo e importe.");
                        }, "No se confirmó. Reenvíe la misma solicitud.");
            });
            vista.setVisible(true);
        });
        menu.alSalir(() -> {
            ejecutar(valor -> menu.setEnabled(!valor), login::mostrarMensaje, () -> { cerrarSesion(); return true; },
                    aceptado -> { menu.dispose(); login.mostrarMensaje("Sesión cerrada."); login.setVisible(true); },
                    "Sesión cerrada localmente; el servidor la expirará.");
        });
        menu.setVisible(true);
    }

    private <T> void ejecutar(Consumer<Boolean> ocupada, Consumer<String> mensaje,
            Supplier<T> tarea, Consumer<T> resultado, String fallo) {
        ocupada.accept(true);
        mensaje.accept("Procesando...");
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() {
                return tarea.get();
            }

            @Override
            protected void done() {
                try {
                    resultado.accept(get());
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    mensaje.accept(fallo);
                } catch (ExecutionException error) {
                    Throwable causa = error.getCause();
                    mensaje.accept(causa instanceof IllegalArgumentException
                            || causa instanceof IllegalStateException ? causa.getMessage() : fallo);
                } finally {
                    ocupada.accept(false);
                }
            }
        }.execute();
    }

    public boolean iniciarSesion(String usuario, char[] contrasena) {
        autenticado = false;
        try {
            if (usuario == null || usuario.isBlank() || usuario.strip().length() > 30
                    || contrasena == null || contrasena.length == 0 || contrasena.length > 1024) {
                throw new IllegalArgumentException("Ingrese usuario y contraseña válidos.");
            }
            try {
                token = servicio.crearSesion(usuario.strip(), new String(contrasena));
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo conectar con EurekaBank.", e);
            }
            autenticado = token != null && !token.isBlank();
            return autenticado;
        } finally {
            if (contrasena != null) {
                Arrays.fill(contrasena, '\0');
            }
        }
    }

    public List<Movimiento> consultarMovimientos(String cuenta) {
        exigirSesion();
        return servicio.traerMovimientos(validarCuenta(cuenta));
    }

    public boolean registrarDeposito(String cuenta, String textoImporte) {
        exigirSesion();
        String numero = validarCuenta(cuenta);
        if (textoImporte == null || !textoImporte.strip().matches("[0-9]+(?:[.,][0-9]{1,2})?")) {
            throw new IllegalArgumentException("Ingrese un importe positivo con hasta dos decimales.");
        }
        BigDecimal importe = new BigDecimal(textoImporte.strip().replace(',', '.')).setScale(2);
        if (importe.signum() <= 0 || importe.precision() > 12) {
            throw new IllegalArgumentException("El importe debe estar entre 0,01 y 9.999.999.999,99.");
        }
        return servicio.regDeposito(numero, importe) == 1;
    }

    public boolean transferir(String origen, String destino, String textoImporte) {
        exigirSesion();
        String origenValido = validarCuenta(origen), destinoValido = validarCuenta(destino);
        if (origenValido.length() != 8 || destinoValido.length() != 8 || origenValido.equals(destinoValido))
            throw new IllegalArgumentException("Use dos cuentas distintas de ocho dígitos.");
        if (textoImporte == null || !textoImporte.strip().matches("[0-9]+(?:[.,][0-9]{1,2})?"))
            throw new IllegalArgumentException("Ingrese un importe positivo con hasta dos decimales.");
        BigDecimal importe = new BigDecimal(textoImporte.strip().replace(',', '.')).setScale(2);
        if (importe.signum() <= 0 || importe.precision() > 12)
            throw new IllegalArgumentException("El importe debe estar entre 0,01 y 9.999.999.999,99.");
        if (transferenciaPendiente == null || !transferenciaPendiente.coincide(origenValido, destinoValido, importe))
            transferenciaPendiente = new TransferenciaPendiente(origenValido, destinoValido, importe, UUID.randomUUID());
        try {
            boolean registrada = servicio.transferir(token, origenValido, destinoValido, importe, transferenciaPendiente.clave());
            transferenciaPendiente = null;
            return registrada;
        } catch (java.io.IOException e) { throw new IllegalStateException("No se confirmó. Reenvíe la misma solicitud.", e); }
    }

    public void cerrarSesion() {
        String actual = token; token = null; transferenciaPendiente = null;
        autenticado = false;
        try { if (actual != null) servicio.cerrarSesion(actual); }
        catch (java.io.IOException e) { throw new IllegalStateException("Sesión cerrada localmente; el servidor la expirará.", e); }
    }

    private record TransferenciaPendiente(String origen, String destino, BigDecimal importe, UUID clave) {
        boolean coincide(String o, String d, BigDecimal i) { return origen.equals(o) && destino.equals(d) && importe.compareTo(i) == 0; }
    }

    private void exigirSesion() {
        if (!autenticado) {
            throw new IllegalStateException("Inicie sesión para realizar esta operación.");
        }
    }

    private String validarCuenta(String cuenta) {
        if (cuenta == null || !cuenta.strip().matches("[0-9]{1,8}")) {
            throw new IllegalArgumentException("Ingrese una cuenta de hasta ocho dígitos.");
        }
        return cuenta.strip();
    }
}
