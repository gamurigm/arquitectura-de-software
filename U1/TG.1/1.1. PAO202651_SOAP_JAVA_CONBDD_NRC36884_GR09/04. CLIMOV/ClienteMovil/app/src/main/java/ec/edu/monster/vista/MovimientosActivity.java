package ec.edu.monster.vista;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ec.edu.monster.controlador.EurekaControlador;
import ec.edu.monster.modelo.Movimiento;
import java.util.List;

public final class MovimientosActivity extends VistaBaseActivity {
    private final EurekaControlador controlador = new EurekaControlador();
    private Button consultar;
    private EditText cuenta;
    private TextView resultado;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); encabezado("Movimientos", "Consulte los movimientos de una cuenta.");
        cuenta = campo("Número de cuenta", InputType.TYPE_CLASS_TEXT);
        cuenta.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(8)});
        consultar = accion("Consultar", this::consultarCuenta);
        resultado = salida(); botonSecundario("Volver al menú", this::finish);
    }

    private void consultarCuenta() {
        esperar(consultar);
        controlador.traerMovimientos(cuenta.getText().toString(), this::mostrar, error -> {
            listo(consultar, "Consultar"); mensaje(error);
        });
    }

    private void mostrar(List<Movimiento> movimientos) {
        listo(consultar, "Consultar");
        if (movimientos.isEmpty()) { resultado.setText("La cuenta no registra movimientos."); return; }
        StringBuilder texto = new StringBuilder();
        for (Movimiento m : movimientos) texto.append(m).append("\n");
        resultado.setText(texto.toString().strip());
    }
}
