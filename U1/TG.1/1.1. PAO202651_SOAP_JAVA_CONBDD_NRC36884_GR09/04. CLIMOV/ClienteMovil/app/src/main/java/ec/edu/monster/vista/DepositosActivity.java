package ec.edu.monster.vista;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import ec.edu.monster.controlador.EurekaControlador;

public final class DepositosActivity extends VistaBaseActivity {
    private final EurekaControlador controlador = new EurekaControlador();
    private Spinner cuenta;
    private EditText importe;
    private Button depositar;
    private TextView resultado;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); encabezado("Registrar depósito", "Acredite fondos en una cuenta.");
        cuenta = selectorCuentas();
        importe = campo("Importe (USD)", android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        depositar = accion("Registrar depósito", this::registrar);
        resultado = salida(); botonSecundario("Volver al menú", this::finish);
    }

    private void registrar() {
        esperar(depositar);
        controlador.registrarDeposito(getIntent().getStringExtra(MenuActivity.TOKEN), cuentaSeleccionada(cuenta), importe.getText().toString(), exito -> {
            listo(depositar, "Registrar depósito");
            if (exito) { resultado.setText("Depósito registrado."); importe.setText(""); }
            else mensaje("El servidor rechazó el depósito.");
        }, error -> { listo(depositar, "Registrar depósito"); mensaje(error); if (error.contains("sesión caducó")) { startActivity(new android.content.Intent(this, LoginActivity.class)); finish(); } });
    }
}
