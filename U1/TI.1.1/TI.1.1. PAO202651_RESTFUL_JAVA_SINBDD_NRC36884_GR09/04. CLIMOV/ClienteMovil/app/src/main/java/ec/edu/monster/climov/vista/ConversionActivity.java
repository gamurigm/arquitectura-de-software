package ec.edu.monster.climov.vista;

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
import ec.edu.monster.climov.controlador.ControladorCliente;
import ec.edu.monster.climov.modelo.CatalogoResponse;
import ec.edu.monster.climov.modelo.ConversionRequest;
import ec.edu.monster.climov.modelo.ConversionResponse;
import ec.edu.monster.climov.modelo.TokenStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Pantalla principal de conversiones del cliente movil. */
public class ConversionActivity extends AppCompatActivity {

    private static final Map<String, List<String>> RESPALDO = new LinkedHashMap<>();

    static {
        RESPALDO.put("longitud", Arrays.asList("milimetros", "centimetros", "metros", "kilometros", "pies", "yardas", "millas"));
        RESPALDO.put("masa", Arrays.asList("miligramos", "gramos", "kilogramos", "toneladas", "onzas", "libras", "quintales"));
        RESPALDO.put("temperatura", Arrays.asList("celsius", "fahrenheit", "kelvin", "rankine", "newton", "reaumur"));
    }

    private TokenStore store;
    private ControladorCliente service;
    private Map<String, List<String>> catalogo = new LinkedHashMap<>(RESPALDO);

    private Spinner spinnerMagnitud;
    private Spinner spinnerOrigen;
    private Spinner spinnerDestino;
    private EditText campoValor;
    private TextView textoResultado;
    private TextView textoMensaje;
    private TextView textoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        store = new TokenStore(this);
        if (store.token() == null) {
            irALogin();
            return;
        }
        service = new ControladorCliente(store.baseUrl());

        spinnerMagnitud = findViewById(R.id.spinnerMagnitud);
        spinnerOrigen = findViewById(R.id.spinnerOrigen);
        spinnerDestino = findViewById(R.id.spinnerDestino);
        campoValor = findViewById(R.id.campoValor);
        textoResultado = findViewById(R.id.textoResultado);
        textoMensaje = findViewById(R.id.textoMensaje);
        textoUsuario = findViewById(R.id.textoUsuario);

        textoUsuario.setText("Usuario: " + store.usuario());

        ArrayAdapter<String> adapterMagnitud = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Longitud", "Masa", "Temperatura"});
        adapterMagnitud.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMagnitud.setAdapter(adapterMagnitud);
        spinnerMagnitud.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                actualizarUnidades();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        ((Button) findViewById(R.id.botonConvertir)).setOnClickListener(v -> convertir());
        ((Button) findViewById(R.id.botonSalir)).setOnClickListener(v -> {
            store.limpiar();
            irALogin();
        });

        actualizarUnidades();
        cargarCatalogo();
    }

    private String magnitudSeleccionada() {
        return spinnerMagnitud.getSelectedItem().toString().toLowerCase();
    }

    private void actualizarUnidades() {
        List<String> unidades = catalogo.getOrDefault(magnitudSeleccionada(),
                RESPALDO.getOrDefault(magnitudSeleccionada(), new ArrayList<>()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, unidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrigen.setAdapter(adapter);
        spinnerDestino.setAdapter(adapter);
        textoResultado.setText("");
        textoMensaje.setText("");
    }

    private void cargarCatalogo() {
        service.catalogo().enqueue(new Callback<CatalogoResponse>() {
            @Override
            public void onResponse(Call<CatalogoResponse> call, Response<CatalogoResponse> response) {
                CatalogoResponse cuerpo = response.body();
                if (response.isSuccessful() && cuerpo != null && cuerpo.getMagnitudes() != null
                        && !cuerpo.getMagnitudes().isEmpty()) {
                    catalogo = cuerpo.getMagnitudes();
                    actualizarUnidades();
                }
            }

            @Override
            public void onFailure(Call<CatalogoResponse> call, Throwable t) {
                // se mantiene el catalogo de respaldo
            }
        });
    }

    private void convertir() {
        double valor;
        try {
            valor = Double.parseDouble(campoValor.getText().toString().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un valor numerico valido", Toast.LENGTH_SHORT).show();
            return;
        }
        String magnitud = magnitudSeleccionada();
        String origen = spinnerOrigen.getSelectedItem().toString();
        String destino = spinnerDestino.getSelectedItem().toString();
        ConversionRequest request = new ConversionRequest(valor, origen, destino);

        Call<ConversionResponse> llamada = service.convertir(magnitud, store.bearer(), request);

        textoResultado.setText("");
        textoMensaje.setText("");

        llamada.enqueue(new Callback<ConversionResponse>() {
            @Override
            public void onResponse(Call<ConversionResponse> call, Response<ConversionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ConversionResponse cuerpo = response.body();
                    textoResultado.setText(String.valueOf(cuerpo.getResultado()));
                    textoMensaje.setText(cuerpo.getMensaje());
                } else if (response.code() == 401) {
                    Toast.makeText(ConversionActivity.this, "Sesion expirada", Toast.LENGTH_SHORT).show();
                    store.limpiar();
                    irALogin();
                } else {
                    textoMensaje.setText("Error del servidor: HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ConversionResponse> call, Throwable t) {
                textoMensaje.setText("Error de conexion: " + t.getMessage());
            }
        });
    }

    private void irALogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
