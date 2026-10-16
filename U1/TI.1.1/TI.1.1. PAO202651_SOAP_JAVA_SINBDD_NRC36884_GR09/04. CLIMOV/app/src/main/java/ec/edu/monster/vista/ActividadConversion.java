package ec.edu.monster.vista;

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
import ec.edu.monster.controlador.ControladorConversion;
import ec.edu.monster.modelo.Sesion;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ActividadConversion extends AppCompatActivity {

    private static final String[] TIPOS_VISIBLES = {"Longitud", "Masa", "Temperatura"};
    private static final String[] TIPOS_SERVICIO = {"longitud", "masa", "temperatura"};
    private static final String[][] UNIDADES = {
        {"milimetros", "centimetros", "metros", "kilometros", "pies", "yardas", "millas"},
        {"miligramos", "gramos", "kilogramos", "toneladas", "onzas", "libras", "quintales"},
        {"celsius", "fahrenheit", "kelvin", "rankine", "newton", "reaumur"}
    };

    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor();
    private Sesion sesion;
    private ControladorConversion controlador;
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
        controlador = new ControladorConversion(getApplicationContext(), sesion.obtenerToken());

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
            public void onItemSelected(AdapterView<?> parent, View vista, int position, long id) {
                selectorOrigen.setAdapter(adaptador(UNIDADES[position]));
                selectorDestino.setAdapter(adaptador(UNIDADES[position]));
                selectorDestino.setSelection(1);
                campoResultado.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        botonConvertir.setOnClickListener(v -> convertir());
        findViewById(R.id.botonIntercambiar).setOnClickListener(v -> {
            int origen = selectorOrigen.getSelectedItemPosition();
            selectorOrigen.setSelection(selectorDestino.getSelectedItemPosition());
            selectorDestino.setSelection(origen);
            campoResultado.setText("");
            textoEstado.setText("");
        });
        campoValor.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { limpiarResultado(); }
            @Override public void afterTextChanged(android.text.Editable s) { }
        });
        android.widget.AdapterView.OnItemSelectedListener cambioUnidad = new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View vista, int position, long id) { limpiarResultado(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        };
        selectorOrigen.setOnItemSelectedListener(cambioUnidad);
        selectorDestino.setOnItemSelectedListener(cambioUnidad);
        campoValor.setOnEditorActionListener((v, accion, evento) -> {
            if (accion != android.view.inputmethod.EditorInfo.IME_ACTION_DONE) return false;
            convertir();
            return true;
        });
        findViewById(R.id.botonCerrarSesion).setOnClickListener(v -> volverAlLogin(null));
    }

    private ArrayAdapter<String> adaptador(String[] opciones) {
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this, R.layout.elemento_opcion, opciones);
        adaptador.setDropDownViewResource(R.layout.elemento_opcion_desplegable);
        return adaptador;
    }

    private void convertir() {
        if (!botonConvertir.isEnabled()) return;
        textoEstado.setTextColor(android.graphics.Color.parseColor("#FF8585"));
        campoResultado.setText("");
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
        if (selectorTipo.getSelectedItemPosition() < 0
                || selectorOrigen.getSelectedItem() == null || selectorDestino.getSelectedItem() == null) {
            textoEstado.setText("Seleccione el tipo y las unidades de conversión.");
            return;
        }
        if (selectorTipo.getSelectedItemPosition() == 1 && valor < 0) {
            textoEstado.setText("La masa no puede ser negativa.");
            return;
        }

        String tipo = TIPOS_SERVICIO[selectorTipo.getSelectedItemPosition()];
        String origen = selectorOrigen.getSelectedItem().toString();
        String destino = selectorDestino.getSelectedItem().toString();
        establecerOcupado(true);
        ((android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(campoValor.getWindowToken(), 0);
        textoEstado.setText("Convirtiendo…");

        ejecutor.execute(() -> {
            try {
                double resultado = controlador.convertir(tipo, valor, origen, destino);
                if (!Double.isFinite(resultado)) {
                    throw new IllegalArgumentException("El resultado excede el rango permitido.");
                }
                runOnUiThread(() -> {
                    campoResultado.setText(formatear(resultado) + " " + destino);
                    textoEstado.setTextColor(android.graphics.Color.parseColor("#52E0D2"));
                    textoEstado.setText("Conversión completada.");
                    establecerOcupado(false);
                    campoResultado.requestRectangleOnScreen(new android.graphics.Rect(
                            0, 0, campoResultado.getWidth(), campoResultado.getHeight()), false);
                });
            } catch (Exception ex) {
                runOnUiThread(() -> {
                    establecerOcupado(false);
                    if (tokenVencido(ex)) {
                        volverAlLogin("La sesión venció. Inicie sesión nuevamente.");
                    } else {
                        textoEstado.setText(mensajeError(ex));
                    }
                });
            }
        });
    }

    private void limpiarResultado() {
        campoResultado.setText("");
        textoEstado.setText("");
    }

    private void establecerOcupado(boolean ocupado) {
        botonConvertir.setEnabled(!ocupado);
        botonConvertir.setText(ocupado ? "Convirtiendo…" : "Convertir ahora");
        selectorTipo.setEnabled(!ocupado);
        selectorOrigen.setEnabled(!ocupado);
        selectorDestino.setEnabled(!ocupado);
        campoValor.setEnabled(!ocupado);
        findViewById(R.id.botonIntercambiar).setEnabled(!ocupado);
        findViewById(R.id.botonCerrarSesion).setEnabled(!ocupado);
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
