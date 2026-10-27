package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import ec.edu.monster.controlador.EurekaControlador;

public final class MenuActivity extends VistaBaseActivity {
    static final String USUARIO = "usuario";
    static final String TOKEN = "token";
    static final String CUENTAS = "cuentas";
    static final String ETIQUETAS_CUENTAS = "etiquetasCuentas";
    private final EurekaControlador controlador = new EurekaControlador();

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        String[] cuentas = getIntent().getStringArrayExtra(CUENTAS);
        encabezado("Operaciones", cuentas == null || cuentas.length == 0
                ? "Hola, " + getIntent().getStringExtra(USUARIO) + ". No hay cuentas activas asignadas."
                : "Hola, " + getIntent().getStringExtra(USUARIO) + ". Elija una operación.");
        accion("Consultar movimientos", () -> abrir(MovimientosActivity.class));
        accion("Registrar depósito", () -> abrir(DepositosActivity.class));
        accion("Transferir", () -> abrir(TransferenciasActivity.class));
        Button salir = botonSecundario("Cerrar sesión", () -> {
            controlador.cerrarSesion(getIntent().getStringExtra(TOKEN));
            startActivity(new Intent(this, LoginActivity.class)); finish();
        });
    }

    private void abrir(Class<?> vista) {
        startActivity(new Intent(this, vista)
                .putExtra(USUARIO, getIntent().getStringExtra(USUARIO))
                .putExtra(TOKEN, getIntent().getStringExtra(TOKEN))
                .putExtra(CUENTAS, getIntent().getStringArrayExtra(CUENTAS))
                .putExtra(ETIQUETAS_CUENTAS, getIntent().getStringArrayExtra(ETIQUETAS_CUENTAS)));
    }
}
