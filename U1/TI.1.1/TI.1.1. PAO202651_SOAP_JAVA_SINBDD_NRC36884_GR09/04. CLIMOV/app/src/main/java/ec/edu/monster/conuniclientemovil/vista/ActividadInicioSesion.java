package ec.edu.monster.conuniclientemovil.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.conuniclientemovil.R;
import ec.edu.monster.conuniclientemovil.controlador.ControladorAutenticacion;
import ec.edu.monster.conuniclientemovil.modelo.Sesion;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ActividadInicioSesion extends AppCompatActivity {

    private final ExecutorService ejecutor = Executors.newSingleThreadExecutor();
    private EditText campoUsuario;
    private EditText campoContrasenia;
    private Button botonIngreso;

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.actividad_inicio_sesion);
        campoUsuario = findViewById(R.id.campoUsuario);
        campoContrasenia = findViewById(R.id.campoContrasenia);
        botonIngreso = findViewById(R.id.botonInicioSesion);
        botonIngreso.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String usuario = campoUsuario.getText().toString().trim();
        String contrasenia = campoContrasenia.getText().toString();
        if (usuario.isEmpty() || contrasenia.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        botonIngreso.setEnabled(false);
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
                    Toast.makeText(this, mensajeError(ex), Toast.LENGTH_LONG).show();
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
