package ec.edu.monster.conuniclientemovil.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.conuniclientemovil.R;
import ec.edu.monster.conuniclientemovil.modelo.Sesion;
import ec.edu.monster.conuniclientemovil.servicios.ServicioSOAP;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ActividadConversion extends AppCompatActivity {

    private static final String[] TIPOS_VISIBLES = {"Longitud", "Masa", "Temperatura"};
    private static final String[] TIPOS_SERVICIO = {"longitud", "masa", "temperatura"};
    private static final String[][] UNIDADES = {
        {"milimetro", "centimetro", "metro", "kilometro", "yarda"},
        {"miligramo", "gramo", "kilogramo", "tonelada", "onza"},
        {"celsius", "fahrenheit", "kelvin", "rankine"}
    };

    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor();
    private Sesion sesion;
    private Spinner selectorTipo;
    private Spinner selectorOrigen;
    private Spinner selectorDestino;
    private EditText campoValor;
    private EditText campoResultado;
    private Button botonConvertir;
    private TextView textoEstado;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.actividad_conversion);
        sesion = new Sesion(getIntent().getStringExtra(Sesion.CLAVE_TOKEN));
        if (!sesion.estaActiva()) {
            volverAlLogin("La sesión terminó. Inicie sesión nuevamente.");
            return;
        }

        selectorTipo = findViewById(R.id.selectorTipoConversion);
        selectorOrigen = findViewById(R.id.selectorUnidadOrigen);
        selectorDestino = findViewById(R.id.selectorUnidadDestino);
        campoValor = findViewById(R.id.campoValor);
        campoResultado = findViewById(R.id.campoResultado);
        botonConvertir = findViewById(R.id.botonConvertir);
        textoEstado = findViewById(R.id.textoEstado);

        selectorTipo.setAdapter(adaptador(TIPOS_VISIBLES));
        selectorTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectorOrigen.setAdapter(adaptador(UNIDADES[position]));
                selectorDestino.setAdapter(adaptador(UNIDADES[position]));
                campoResultado.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        botonConvertir.setOnClickListener(v -> convertir());
        findViewById(R.id.botonCerrarSesion).setOnClickListener(v -> volverAlLogin(null));
    }

    private ArrayAdapter<String> adaptador(String[] opciones) {
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, opciones);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adaptador;
    }

    private void convertir() {
        String valorTexto = campoValor.getText().toString().trim().replace(',', '.');
        if (valorTexto.isEmpty()) {
            textoEstado.setText("Ingrese el valor que desea convertir.");
            return;
        }

        final double valor;
        try {
            valor = Double.parseDouble(valorTexto);
            if (!Double.isFinite(valor)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            textoEstado.setText("Ingrese un número válido.");
            return;
        }

        String tipo = TIPOS_SERVICIO[selectorTipo.getSelectedItemPosition()];
        String origen = selectorOrigen.getSelectedItem().toString();
        String destino = selectorDestino.getSelectedItem().toString();
        botonConvertir.setEnabled(false);
        textoEstado.setText("Convirtiendo…");

        ejecutor.execute(() -> {
            try {
                double resultado = new ServicioSOAP(getApplicationContext(), sesion.obtenerToken())
                        .convertir(tipo, valor, origen, destino);
                runOnUiThread(() -> {
                    campoResultado.setText(formatear(resultado));
                    textoEstado.setText("Conversión completada.");
                    botonConvertir.setEnabled(true);
                });
            } catch (Exception ex) {
                runOnUiThread(() -> {
                    botonConvertir.setEnabled(true);
                    if (tokenVencido(ex)) {
                        volverAlLogin("La sesión venció. Inicie sesión nuevamente.");
                    } else {
                        textoEstado.setText(mensajeError(ex));
                    }
                });
            }
        });
    }

    private static String formatear(double valor) {
        DecimalFormat formato = new DecimalFormat("0.########", DecimalFormatSymbols.getInstance(Locale.US));
        return formato.format(valor);
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
        String mensaje = error.getMessage();
        if (mensaje == null || mensaje.isBlank()) {
            return "No fue posible realizar la conversión.";
        }
        return mensaje;
    }

    private void volverAlLogin(String mensaje) {
        if (mensaje != null) {
            android.widget.Toast.makeText(this, mensaje, android.widget.Toast.LENGTH_LONG).show();
        }
        startActivity(new Intent(this, ActividadInicioSesion.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        ejecutor.shutdownNow();
        super.onDestroy();
    }
}
