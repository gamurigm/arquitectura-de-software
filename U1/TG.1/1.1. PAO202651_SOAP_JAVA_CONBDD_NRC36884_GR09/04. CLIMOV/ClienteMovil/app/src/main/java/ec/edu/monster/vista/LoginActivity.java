package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import ec.edu.monster.controlador.EurekaControlador;
import ec.edu.monster.modelo.Cuenta;

public final class LoginActivity extends VistaBaseActivity {
    private final EurekaControlador controlador = new EurekaControlador();
    private EditText usuario;
    private EditText contrasena;
    private Button ingresar;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); encabezado("Iniciar sesión", "Ingrese sus credenciales de acceso.");
        usuario = campo("Usuario", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        usuario.setMaxLines(1);
        contrasena = campo("Contraseña", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ingresar = accion("Ingresar", this::iniciar);
    }

    private void iniciar() {
        esperar(ingresar);
        controlador.iniciarSesion(usuario.getText().toString(), contrasena.getText().toString(), token -> {
            listo(ingresar, "Ingresar");
            if (token != null && !token.isBlank()) {
                ingresar.setEnabled(false);
                controlador.traerCuentas(token, cuentas -> {
                    String[] numeros = cuentas.stream().map(Cuenta::numero).toArray(String[]::new);
                    String[] etiquetas = cuentas.stream().map(Cuenta::toString).toArray(String[]::new);
                    startActivity(new Intent(this, MenuActivity.class)
                            .putExtra(MenuActivity.USUARIO, usuario.getText().toString().strip())
                            .putExtra(MenuActivity.TOKEN, token)
                            .putExtra(MenuActivity.CUENTAS, numeros)
                            .putExtra(MenuActivity.ETIQUETAS_CUENTAS, etiquetas));
                    finish();
                }, error -> { listo(ingresar, "Ingresar"); mensaje(error); });
            } else mensaje("Credenciales incorrectas o servicio no disponible.");
            contrasena.setText("");
        });
    }
}
