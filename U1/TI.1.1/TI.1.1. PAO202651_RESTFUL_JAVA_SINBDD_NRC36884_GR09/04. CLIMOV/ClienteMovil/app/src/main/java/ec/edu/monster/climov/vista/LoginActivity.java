package ec.edu.monster.climov.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorCliente;
import ec.edu.monster.climov.modelo.LoginRequest;
import ec.edu.monster.climov.modelo.TokenResponse;
import ec.edu.monster.climov.modelo.TokenStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Pantalla de inicio de sesion del cliente movil. */
public class LoginActivity extends AppCompatActivity {

    private EditText campoBaseUrl;
    private EditText campoUsuario;
    private EditText campoClave;
    private TextView textoError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoBaseUrl = findViewById(R.id.campoBaseUrl);
        campoUsuario = findViewById(R.id.campoUsuario);
        campoClave = findViewById(R.id.campoClave);
        textoError = findViewById(R.id.textoError);
        Button botonLogin = findViewById(R.id.botonLogin);

        TokenStore store = new TokenStore(this);
        campoBaseUrl.setText(store.baseUrl());

        botonLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String baseUrl = campoBaseUrl.getText().toString().trim();
        String usuario = campoUsuario.getText().toString().trim();
        String clave = campoClave.getText().toString();
        if (baseUrl.isEmpty()) {
            baseUrl = "http://10.0.2.2:8080";
        }
        textoError.setText("");

        ControladorCliente service = new ControladorCliente(baseUrl);
        final String urlFinal = baseUrl;
        service.login(new LoginRequest(usuario, clave)).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                TokenResponse cuerpo = response.body();
                if (response.isSuccessful() && cuerpo != null && cuerpo.getToken() != null) {
                    new TokenStore(LoginActivity.this)
                            .guardarSesion(urlFinal, cuerpo.getUsuario(), cuerpo.getToken());
                    startActivity(new Intent(LoginActivity.this, ConversionActivity.class));
                    finish();
                } else {
                    textoError.setText("Credenciales invalidas o servidor no disponible.");
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                textoError.setText("Error de conexion: " + t.getMessage());
            }
        });
    }
}
