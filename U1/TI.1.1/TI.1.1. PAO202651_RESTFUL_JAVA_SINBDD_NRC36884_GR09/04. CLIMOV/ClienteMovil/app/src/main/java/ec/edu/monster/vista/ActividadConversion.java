package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ec.edu.monster.climov.R;
import ec.edu.monster.controlador.ControladorCliente;
import ec.edu.monster.modelo.RespuestaCatalogo;
import ec.edu.monster.modelo.SolicitudConversion;
import ec.edu.monster.modelo.RespuestaConversion;
import ec.edu.monster.modelo.AlmacenTokens;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Pantalla principal de conversiones del cliente movil. */
public class ActividadConversion extends AppCompatActivity {

    private static final Map<String, List<String>> RESPALDO = new LinkedHashMap<>();

    static {
        RESPALDO.put("longitud", Arrays.asList("milimetros", "centimetros", "metros", "kilometros", "pies", "yardas", "millas"));
        RESPALDO.put("masa", Arrays.asList("miligramos", "gramos", "kilogramos", "toneladas", "onzas", "libras", "quintales"));
        RESPALDO.put("temperatura", Arrays.asList("celsius", "fahrenheit", "kelvin", "rankine", "newton", "reaumur"));
    }

    private AlmacenTokens almacen;
    private ControladorCliente controlador;
    private Map<String, List<String>> catalogo = new LinkedHashMap<>(RESPALDO);

    private Spinner spinnerMagnitud;
    private Spinner spinnerOrigen;
    private Spinner spinnerDestino;
    private EditText campoValor;
    private TextView textoResultado;
    private TextView textoMensaje;
    private TextView textoUsuario;
    private Button botonConvertir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_conversion);

        almacen = new AlmacenTokens(this);
        if (almacen.token() == null) {
            irALogin();
            return;
        }
        controlador = new ControladorCliente(almacen.urlBase());

        spinnerMagnitud = findViewById(R.id.spinnerMagnitud);
        spinnerOrigen = findViewById(R.id.spinnerOrigen);
        spinnerDestino = findViewById(R.id.spinnerDestino);
        campoValor = findViewById(R.id.campoValor);
        textoResultado = findViewById(R.id.textoResultado);
        textoMensaje = findViewById(R.id.textoMensaje);
        textoUsuario = findViewById(R.id.textoUsuario);
        botonConvertir = findViewById(R.id.botonConvertir);

        textoUsuario.setText("Hola, " + almacen.usuario() + ". Elige qué convertir.");

        ArrayAdapter<String> adapterMagnitud = new ArrayAdapter<>(this,
                R.layout.elemento_opcion,
                new String[]{"Longitud", "Masa", "Temperatura"});
        adapterMagnitud.setDropDownViewResource(R.layout.elemento_opcion_desplegable);
        spinnerMagnitud.setAdapter(adapterMagnitud);
        spinnerMagnitud.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View vista, int position, long id) {
                actualizarUnidades();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        botonConvertir.setOnClickListener(v -> convertir());
        findViewById(R.id.botonIntercambiar).setOnClickListener(v -> {
            int origen = spinnerOrigen.getSelectedItemPosition();
            spinnerOrigen.setSelection(spinnerDestino.getSelectedItemPosition());
            spinnerDestino.setSelection(origen);
            textoResultado.setText("");
            textoMensaje.setText("");
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
        spinnerOrigen.setOnItemSelectedListener(cambioUnidad);
        spinnerDestino.setOnItemSelectedListener(cambioUnidad);
        campoValor.setOnEditorActionListener((v, accion, evento) -> {
            if (accion != android.view.inputmethod.EditorInfo.IME_ACTION_DONE) return false;
            convertir();
            return true;
        });
        ((Button) findViewById(R.id.botonSalir)).setOnClickListener(v -> {
            almacen.limpiar();
            irALogin();
        });

        actualizarUnidades();
        cargarCatalogo();
    }

    private String magnitudSeleccionada() {
        Object seleccion = spinnerMagnitud.getSelectedItem();
        return seleccion == null ? "" : seleccion.toString().toLowerCase();
    }

    private void actualizarUnidades() {
        List<String> unidades = catalogo.getOrDefault(magnitudSeleccionada(),
                RESPALDO.getOrDefault(magnitudSeleccionada(), new ArrayList<>()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.elemento_opcion, unidades);
        adapter.setDropDownViewResource(R.layout.elemento_opcion_desplegable);
        spinnerOrigen.setAdapter(adapter);
        spinnerDestino.setAdapter(adapter);
        if (unidades.size() > 1) spinnerDestino.setSelection(1);
        textoResultado.setText("");
        textoMensaje.setText("");
    }

    private void cargarCatalogo() {
        controlador.obtenerCatalogo().enqueue(new Callback<RespuestaCatalogo>() {
            @Override
            public void onResponse(Call<RespuestaCatalogo> llamada, Response<RespuestaCatalogo> respuesta) {
                RespuestaCatalogo cuerpo = respuesta.body();
                if (respuesta.isSuccessful() && cuerpo != null && cuerpo.obtenerMagnitudes() != null
                        && !cuerpo.obtenerMagnitudes().isEmpty()) {
                    catalogo = cuerpo.obtenerMagnitudes();
                    actualizarUnidades();
                }
            }

            @Override
            public void onFailure(Call<RespuestaCatalogo> llamada, Throwable t) {
                // se mantiene el catalogo de respaldo
            }
        });
    }

    private void convertir() {
        if (!botonConvertir.isEnabled()) return;
        textoResultado.setText("");
        textoMensaje.setText("");
        textoMensaje.setTextColor(android.graphics.Color.parseColor("#FF8585"));
        double valor;
        try {
            valor = Double.parseDouble(campoValor.getText().toString().trim().replace(",", "."));
            if (!Double.isFinite(valor)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un número finito válido", Toast.LENGTH_SHORT).show();
            return;
        }
        String magnitud = magnitudSeleccionada();
        if (magnitud.isEmpty() || spinnerOrigen.getSelectedItem() == null || spinnerDestino.getSelectedItem() == null) {
            textoMensaje.setText("Seleccione magnitud y ambas unidades.");
            return;
        }
        if ("masa".equals(magnitud) && valor < 0) {
            textoMensaje.setText("La masa no puede ser negativa.");
            return;
        }
        String origen = spinnerOrigen.getSelectedItem().toString();
        String destino = spinnerDestino.getSelectedItem().toString();
        SolicitudConversion solicitud = new SolicitudConversion(valor, origen, destino);

        Call<RespuestaConversion> llamada = controlador.convertir(magnitud, almacen.bearer(), solicitud);
        establecerOcupado(true);
        ((android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(campoValor.getWindowToken(), 0);

        textoResultado.setText("");
        textoMensaje.setText("");

        llamada.enqueue(new Callback<RespuestaConversion>() {
            @Override
            public void onResponse(Call<RespuestaConversion> llamada, Response<RespuestaConversion> respuesta) {
                establecerOcupado(false);
                if (respuesta.isSuccessful() && respuesta.body() != null) {
                    RespuestaConversion cuerpo = respuesta.body();
                    if (!Double.isFinite(cuerpo.obtenerResultado())) {
                        textoMensaje.setText("El servidor devolvió un resultado inválido.");
                        return;
                    }
                    textoResultado.setText(cuerpo.obtenerResultado() + " " + destino);
                    textoMensaje.setTextColor(android.graphics.Color.parseColor("#52E0D2"));
                    textoMensaje.setText(cuerpo.obtenerMensaje());
                    textoResultado.requestRectangleOnScreen(new android.graphics.Rect(
                            0, 0, textoResultado.getWidth(), textoResultado.getHeight()), false);
                } else if (respuesta.code() == 401) {
                    Toast.makeText(ActividadConversion.this, "Sesion expirada", Toast.LENGTH_SHORT).show();
                    almacen.limpiar();
                    irALogin();
                } else {
                    textoMensaje.setText("Error del servidor: HTTP " + respuesta.code());
                }
            }

            @Override
            public void onFailure(Call<RespuestaConversion> llamada, Throwable t) {
                establecerOcupado(false);
                textoMensaje.setText("Error de conexion: " + t.getMessage());
            }
        });
    }

    private void limpiarResultado() {
        textoResultado.setText("");
        textoMensaje.setText("");
    }

    private void establecerOcupado(boolean ocupado) {
        botonConvertir.setEnabled(!ocupado);
        botonConvertir.setText(ocupado ? "Convirtiendo…" : "Convertir ahora");
        spinnerMagnitud.setEnabled(!ocupado);
        spinnerOrigen.setEnabled(!ocupado);
        spinnerDestino.setEnabled(!ocupado);
        campoValor.setEnabled(!ocupado);
        findViewById(R.id.botonIntercambiar).setEnabled(!ocupado);
        findViewById(R.id.botonSalir).setEnabled(!ocupado);
    }

    private void irALogin() {
        startActivity(new Intent(this, ActividadInicioSesion.class));
        finish();
    }
}
