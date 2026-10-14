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

        textoUsuario.setText("Usuario: " + almacen.usuario());

        ArrayAdapter<String> adapterMagnitud = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Longitud", "Masa", "Temperatura"});
        adapterMagnitud.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        ((Button) findViewById(R.id.botonConvertir)).setOnClickListener(v -> convertir());
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
                android.R.layout.simple_spinner_item, unidades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrigen.setAdapter(adapter);
        spinnerDestino.setAdapter(adapter);
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

        textoResultado.setText("");
        textoMensaje.setText("");

        llamada.enqueue(new Callback<RespuestaConversion>() {
            @Override
            public void onResponse(Call<RespuestaConversion> llamada, Response<RespuestaConversion> respuesta) {
                if (respuesta.isSuccessful() && respuesta.body() != null) {
                    RespuestaConversion cuerpo = respuesta.body();
                    if (!Double.isFinite(cuerpo.obtenerResultado())) {
                        textoMensaje.setText("El servidor devolvió un resultado inválido.");
                        return;
                    }
                    textoResultado.setText(String.valueOf(cuerpo.obtenerResultado()));
                    textoMensaje.setText(cuerpo.obtenerMensaje());
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
                textoMensaje.setText("Error de conexion: " + t.getMessage());
            }
        });
    }

    private void irALogin() {
        startActivity(new Intent(this, ActividadInicioSesion.class));
        finish();
    }
}
