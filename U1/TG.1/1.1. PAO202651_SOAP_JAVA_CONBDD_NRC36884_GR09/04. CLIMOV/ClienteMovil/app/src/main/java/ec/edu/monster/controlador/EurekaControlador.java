package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.service.EurekaService;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import android.os.Handler;
import android.os.Looper;

public final class EurekaControlador {
    private static final ExecutorService RED = Executors.newFixedThreadPool(2);
    private static final Handler UI = new Handler(Looper.getMainLooper());
    private final EurekaService servicio;

    public EurekaControlador() { this(new EurekaService()); }
    public EurekaControlador(EurekaService servicio) { this.servicio = servicio; }

    public void iniciarSesion(String usuario, String contrasena, Consumer<String> respuesta) {
        RED.execute(() -> {
            String token = null;
            try {
                if (usuario != null && !usuario.isBlank() && contrasena != null && !contrasena.isEmpty())
                    token = servicio.crearSesion(usuario.strip(), contrasena);
            } catch (IOException ignored) { }
            String resultado = token;
            UI.post(() -> respuesta.accept(resultado));
        });
    }

    public void traerMovimientos(String token, String cuenta, Consumer<List<Movimiento>> respuesta,
                                 Consumer<String> error) {
        if (cuenta == null || cuenta.isBlank() || cuenta.strip().length() > 8) {
            error.accept("Ingrese una cuenta válida de hasta 8 caracteres.");
            return;
        }
        RED.execute(() -> {
            try {
                List<Movimiento> resultado = servicio.traerMovimientos(token, cuenta.strip());
                UI.post(() -> respuesta.accept(resultado));
            } catch (IOException | RuntimeException e) {
                UI.post(() -> error.accept(tokenExpirado(e) ? "La sesión caducó. Inicie sesión nuevamente." : "No se pudieron consultar los movimientos."));
            }
        });
    }

    public void traerCuentas(String token, Consumer<List<Cuenta>> respuesta, Consumer<String> error) {
        RED.execute(() -> {
            try {
                List<Cuenta> cuentas = servicio.traerCuentas(token);
                UI.post(() -> respuesta.accept(cuentas));
            } catch (IOException | RuntimeException e) {
                UI.post(() -> error.accept(tokenExpirado(e) ? "La sesión caducó. Inicie sesión nuevamente." : "No se pudieron cargar las cuentas."));
            }
        });
    }

    public void registrarDeposito(String token, String cuenta, String texto, Consumer<Boolean> respuesta,
                                  Consumer<String> error) {
        try {
            if (cuenta == null || cuenta.isBlank() || cuenta.strip().length() > 8)
                throw new IllegalArgumentException("Ingrese una cuenta válida de hasta 8 caracteres.");
            BigDecimal importe = new BigDecimal(texto.strip()).setScale(2, RoundingMode.UNNECESSARY);
            if (importe.signum() <= 0 || importe.precision() > 12)
                throw new IllegalArgumentException("El importe debe ser positivo y no superar 10 dígitos.");
            RED.execute(() -> {
                try {
                    boolean resultado = servicio.registrarDeposito(token, cuenta.strip(), importe);
                    UI.post(() -> respuesta.accept(resultado));
                }
                catch (IOException | RuntimeException e) {
                    UI.post(() -> error.accept(tokenExpirado(e) ? "La sesión caducó. Inicie sesión nuevamente." : "Resultado incierto. Consulte movimientos antes de reintentar."));
                }
            });
        } catch (NumberFormatException | ArithmeticException e) {
            error.accept("Ingrese un importe válido con máximo dos decimales.");
        } catch (IllegalArgumentException e) { error.accept(e.getMessage()); }
    }

    public void transferir(String token, String origen, String destino, String texto, UUID clave,
                           Consumer<Boolean> respuesta, Consumer<String> error) {
        try {
            if (token == null || token.isBlank()) throw new IllegalArgumentException("Inicie sesión otra vez.");
            if (origen == null || !origen.matches("[0-9]{8}")
                    || destino == null || !destino.matches("[0-9]{8}"))
                throw new IllegalArgumentException("Ingrese dos cuentas válidas de 8 dígitos.");
            if (origen.equals(destino)) throw new IllegalArgumentException("Las cuentas deben ser distintas.");
            BigDecimal importe = new BigDecimal(texto.strip()).setScale(2, RoundingMode.UNNECESSARY);
            if (importe.signum() <= 0 || importe.precision() > 12)
                throw new IllegalArgumentException("El importe debe ser positivo y no superar 10 dígitos.");
            RED.execute(() -> {
                try {
                    boolean resultado = servicio.transferir(token, origen, destino, importe, clave);
                    UI.post(() -> respuesta.accept(resultado));
                } catch (IOException | RuntimeException e) {
                    UI.post(() -> error.accept(tokenExpirado(e) ? "La sesión caducó. Inicie sesión nuevamente." : "No se confirmó la transferencia. Reintente sin cambiar los datos."));
                }
            });
        } catch (NumberFormatException | ArithmeticException e) {
            error.accept("Ingrese un importe válido con máximo dos decimales.");
        } catch (IllegalArgumentException e) { error.accept(e.getMessage()); }
    }

    public void cerrarSesion(String token) {
        RED.execute(() -> { try { servicio.cerrarSesion(token); } catch (IOException ignored) { } });
    }

    private static boolean tokenExpirado(Throwable error) {
        for (Throwable actual = error; actual != null; actual = actual.getCause())
            if (actual.getMessage() != null && actual.getMessage().contains("Token ausente, inválido o caducado")) return true;
        return false;
    }
}
