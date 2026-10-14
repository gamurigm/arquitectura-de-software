package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ec.edu.monster.climov.R;
import ec.edu.monster.controlador.ControladorCliente;
import ec.edu.monster.servicios.ClienteApi;
import ec.edu.monster.modelo.SolicitudInicioSesion;
import ec.edu.monster.modelo.RespuestaToken;
import ec.edu.monster.modelo.AlmacenTokens;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Pantalla de inicio de sesion del cliente movil. */
public class ActividadInicioSesion extends AppCompatActivity {

    private EditText campoBaseUrl;
    private EditText campoUsuario;
    private EditText campoClave;
    private TextView textoError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_inicio_sesion);

        campoBaseUrl = findViewById(R.id.campoBaseUrl);
        campoUsuario = findViewById(R.id.campoUsuario);
        campoClave = findViewById(R.id.campoClave);
        textoError = findViewById(R.id.textoError);
        Button botonLogin = findViewById(R.id.botonLogin);

        AlmacenTokens store = new AlmacenTokens(this);
        campoBaseUrl.setText(store.urlBase());

        botonLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String urlBase = campoBaseUrl.getText().toString().trim();
        String usuario = campoUsuario.getText().toString().trim();
        String clave = campoClave.getText().toString();
        if (urlBase.isEmpty()) {
            urlBase = "http://10.0.2.2:8080";
        }
        textoError.setText("");
        if (usuario.isBlank() || clave.isBlank()) {
            textoError.setText("Ingrese usuario y contraseña.");
            return;
        }
        try {
            urlBase = ClienteApi.validarUrlBase(urlBase);
        } catch (IllegalArgumentException ex) {
            textoError.setText(ex.getMessage());
            return;
        }

        ControladorCliente service = new ControladorCliente(urlBase);
        final String urlFinal = urlBase;
        service.iniciarSesion(new SolicitudInicioSesion(usuario, clave)).enqueue(new Callback<RespuestaToken>() {
            @Override
            public void onResponse(Call<RespuestaToken> llamada, Response<RespuestaToken> respuesta) {
                RespuestaToken cuerpo = respuesta.body();
                if (respuesta.isSuccessful() && cuerpo != null && cuerpo.obtenerToken() != null) {
                    new AlmacenTokens(ActividadInicioSesion.this)
                            .guardarSesion(urlFinal, cuerpo.obtenerUsuario(), cuerpo.obtenerToken());
                    startActivity(new Intent(ActividadInicioSesion.this, ActividadConversion.class));
                    finish();
                } else {
                    textoError.setText("Credenciales invalidas o servidor no disponible.");
                }
            }

            @Override
            public void onFailure(Call<RespuestaToken> llamada, Throwable t) {
                textoError.setText("Error de conexion: " + t.getMessage());
            }
        });
    }
}
