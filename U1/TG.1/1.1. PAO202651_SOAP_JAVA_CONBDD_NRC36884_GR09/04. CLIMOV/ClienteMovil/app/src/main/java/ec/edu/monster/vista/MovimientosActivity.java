package ec.edu.monster.vista;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import ec.edu.monster.controlador.EurekaControlador;
import ec.edu.monster.modelo.Movimiento;
import java.util.List;

public final class MovimientosActivity extends VistaBaseActivity {
    private final EurekaControlador controlador = new EurekaControlador();
    private Button consultar;
    private Spinner cuenta;
    private TextView resultado;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); encabezado("Movimientos", "Consulte los movimientos de una cuenta.");
        cuenta = selectorCuentas();
        consultar = accion("Consultar", this::consultarCuenta);
        resultado = salida(); botonSecundario("Volver al menú", this::finish);
    }

    private void consultarCuenta() {
        esperar(consultar);
        controlador.traerMovimientos(getIntent().getStringExtra(MenuActivity.TOKEN), cuentaSeleccionada(cuenta), this::mostrar, error -> {
            listo(consultar, "Consultar"); mensaje(error);
            if (error.contains("sesión caducó")) { startActivity(new android.content.Intent(this, LoginActivity.class)); finish(); }
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
