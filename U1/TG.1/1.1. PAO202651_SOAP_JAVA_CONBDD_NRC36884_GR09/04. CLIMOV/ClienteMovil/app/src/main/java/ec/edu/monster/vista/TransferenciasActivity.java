package ec.edu.monster.vista;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ec.edu.monster.controlador.EurekaControlador;
import java.util.UUID;

public final class TransferenciasActivity extends VistaBaseActivity {
    private final EurekaControlador controlador = new EurekaControlador();
    private EditText origen;
    private EditText destino;
    private EditText importe;
    private Button enviar;
    private TextView resultado;
    private String huellaPendiente;
    private String clavePendiente;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        encabezado("Transferir", "El débito y el crédito se registran en una sola operación.");
        origen = campo("Cuenta de origen", InputType.TYPE_CLASS_NUMBER);
        origen.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        destino = campo("Cuenta de destino", InputType.TYPE_CLASS_NUMBER);
        destino.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        importe = campo("Importe (USD)", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        enviar = accion("Transferir", this::transferir);
        resultado = salida();
        botonSecundario("Volver al menú", this::finish);
        if (state != null) {
            huellaPendiente = state.getString("huellaPendiente");
            clavePendiente = state.getString("clavePendiente");
        }
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        state.putString("huellaPendiente", huellaPendiente);
        state.putString("clavePendiente", clavePendiente);
        super.onSaveInstanceState(state);
    }

    private void transferir() {
        String desde = origen.getText().toString().strip();
        String hacia = destino.getText().toString().strip();
        String valor = importe.getText().toString().strip();
        String huella = desde + "|" + hacia + "|" + valor;
        if (!huella.equals(huellaPendiente)) {
            huellaPendiente = huella;
            clavePendiente = UUID.randomUUID().toString();
        }
        esperar(enviar);
        controlador.transferir(getIntent().getStringExtra(MenuActivity.TOKEN), desde, hacia, valor,
                UUID.fromString(clavePendiente), exito -> {
                    listo(enviar, "Transferir");
                    if (exito) {
                        resultado.setText("Transferencia registrada: débito en origen y crédito en destino.");
                        importe.setText("");
                        huellaPendiente = null;
                        clavePendiente = null;
                    } else mensaje("El servidor rechazó la transferencia.");
                }, error -> { listo(enviar, "Transferir"); mensaje(error); });
    }
}
