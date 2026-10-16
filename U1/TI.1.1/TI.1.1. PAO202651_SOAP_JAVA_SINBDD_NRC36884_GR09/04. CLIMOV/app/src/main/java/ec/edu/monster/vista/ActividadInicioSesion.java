package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.conuniclientemovil.R;
import ec.edu.monster.controlador.ControladorAutenticacion;
import ec.edu.monster.modelo.Sesion;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ActividadInicioSesion extends AppCompatActivity {

    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor();
    private EditText campoUsuario;
    private EditText campoContrasenia;
    private Button botonIngreso;
    private TextView textoError;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.actividad_inicio_sesion);
        campoUsuario = findViewById(R.id.campoUsuario);
        campoContrasenia = findViewById(R.id.campoContrasenia);
        botonIngreso = findViewById(R.id.botonInicioSesion);
        textoError = findViewById(R.id.textoError);
        botonIngreso.setOnClickListener(v -> iniciarSesion());
        campoContrasenia.setOnEditorActionListener((v, accion, evento) -> {
            if (accion != android.view.inputmethod.EditorInfo.IME_ACTION_GO) return false;
            iniciarSesion();
            return true;
        });
    }

    private void iniciarSesion() {
        if (!botonIngreso.isEnabled()) return;
        textoError.setText("");
        String usuario = campoUsuario.getText().toString().trim();
        String contrasenia = campoContrasenia.getText().toString();
        if (usuario.isEmpty() || contrasenia.isEmpty()) {
            textoError.setText("Ingrese usuario y contraseña.");
            return;
        }

        botonIngreso.setEnabled(false);
        botonIngreso.setText("Conectando…");
        ejecutor.execute(() -> {
            try {
                String token = new ControladorAutenticacion(getApplicationContext())
                        .iniciarSesion(usuario, contrasenia);
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, ActividadConversion.class);
                    intent.putExtra(Sesion.CLAVE_TOKEN, token);
                    startActivity(intent);
                    finish();
                });
            } catch (Exception ex) {
                runOnUiThread(() -> {
                    botonIngreso.setEnabled(true);
                    botonIngreso.setText("Entrar al conversor");
                    textoError.setText(mensajeError(ex));
                });
            }
        });
    }

    private static String mensajeError(Throwable error) {
        for (Throwable causa = error; causa != null; causa = causa.getCause()) {
            String mensaje = causa.getMessage();
            if (mensaje != null && mensaje.toLowerCase().contains("credenciales incorrectas")) {
                return "Usuario o contraseña incorrectos.";
            }
        }
        return "No se pudo iniciar sesión. Verifique el servidor SOAP y vuelva a intentar.";
    }

    @Override
    protected void onDestroy() {
        ejecutor.shutdownNow();
        super.onDestroy();
    }
}
