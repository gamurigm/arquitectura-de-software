package ec.edu.monster.vista;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ec.edu.monster.controlador.EurekaControlador;

public final class DepositosActivity extends VistaBaseActivity {
    private final EurekaControlador controlador = new EurekaControlador();
    private EditText cuenta;
    private EditText importe;
    private Button depositar;
    private TextView resultado;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); encabezado("Registrar depósito", "Acredite fondos en una cuenta.");
        cuenta = campo("Número de cuenta", InputType.TYPE_CLASS_TEXT);
        cuenta.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(8)});
        importe = campo("Importe (USD)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        depositar = accion("Registrar depósito", this::registrar);
        resultado = salida(); botonSecundario("Volver al menú", this::finish);
    }

    private void registrar() {
        esperar(depositar);
        controlador.registrarDeposito(cuenta.getText().toString(), importe.getText().toString(), exito -> {
            listo(depositar, "Registrar depósito");
            if (exito) { resultado.setText("Depósito registrado."); importe.setText(""); }
            else mensaje("El servidor rechazó el depósito.");
        }, error -> { listo(depositar, "Registrar depósito"); mensaje(error); });
    }
}
