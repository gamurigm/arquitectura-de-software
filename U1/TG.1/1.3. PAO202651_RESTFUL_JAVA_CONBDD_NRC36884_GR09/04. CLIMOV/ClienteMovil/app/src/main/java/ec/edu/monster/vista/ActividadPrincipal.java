package ec.edu.monster.vista;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ec.edu.monster.controlador.ControladorCliente;
import ec.edu.monster.modelo.Movimiento;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ActividadPrincipal extends Activity {
    private static final int FONDO = Color.rgb(11, 16, 32);
    private static final int PANEL = Color.rgb(18, 28, 48);
    private static final int CAMPO = Color.rgb(23, 36, 58);
    private static final int TEXTO = Color.rgb(244, 247, 252);
    private static final int MUTED = Color.rgb(169, 183, 203);
    private static final int TURQUESA = Color.rgb(82, 224, 210);
    private final ControladorCliente controlador = new ControladorCliente();
    private final ExecutorService tareas = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());
    private LinearLayout raiz;
    private EditText usuario;
    private EditText contrasena;
    private EditText cuenta;
    private EditText importe;
    private EditText cuentaOrigen;
    private EditText cuentaDestino;
    private EditText importeTransferencia;
    private TextView estado;
    private TextView resultados;
    private Button botonAccion;

    @Override protected void onCreate(Bundle estadoGuardado) {
        super.onCreate(estadoGuardado);
        getWindow().setStatusBarColor(FONDO);
        getWindow().setNavigationBarColor(FONDO);
        mostrarLogin();
    }

    private void mostrarLogin() {
        iniciarContenido();
        LinearLayout tarjeta = tarjeta();
        raiz.addView(tarjeta);
        TextView marca = texto("MONSTER  /  EUREKABANK", 13, TURQUESA, true);
        tarjeta.addView(marca);
        tarjeta.addView(texto("Banca personal", 29, TEXTO, true));
        tarjeta.addView(texto("Acceso seguro a tus cuentas", 15, MUTED, false));
        usuario = campo("Usuario", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        contrasena = campo("Contraseña", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tarjeta.addView(usuario);
        tarjeta.addView(contrasena);
        botonAccion = boton("Iniciar sesión", true);
        tarjeta.addView(botonAccion);
        estado = texto("", 14, MUTED, false);
        estado.setContentDescription("Estado del acceso");
        tarjeta.addView(estado);
        botonAccion.setOnClickListener(v -> autenticar());
    }

    private void mostrarPanel() {
        iniciarContenido();
        LinearLayout tarjeta = tarjeta();
        raiz.addView(tarjeta);
        LinearLayout encabezado = new LinearLayout(this);
        encabezado.setOrientation(LinearLayout.HORIZONTAL);
        encabezado.setGravity(android.view.Gravity.CENTER_VERTICAL);
        encabezado.addView(texto("EurekaBank", 25, TEXTO, true), new LinearLayout.LayoutParams(0, -2, 1));
        Button salir = boton("Salir", false);
        salir.setContentDescription("Cerrar sesión");
        salir.setOnClickListener(v -> { botonAccion = salir; ejecutar(() -> { controlador.cerrarSesion(); return true; }, r -> mostrarLogin()); });
        encabezado.addView(salir);
        tarjeta.addView(encabezado);
        tarjeta.addView(texto("Sesión de " + controlador.usuarioActual(), 14, MUTED, false));
        cuenta = campo("Número de cuenta (8 dígitos)", InputType.TYPE_CLASS_NUMBER);
        importe = campo("Importe en dólares", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        tarjeta.addView(cuenta);
        tarjeta.addView(importe);
        Button movimientos = boton("Consultar movimientos", true);
        movimientos.setOnClickListener(v -> { botonAccion = (Button) v; consultar(); });
        tarjeta.addView(movimientos);
        Button deposito = boton("Registrar depósito", false);
        deposito.setOnClickListener(v -> { botonAccion = (Button) v; depositar(); });
        tarjeta.addView(deposito);
        cuentaOrigen = campo("Cuenta origen (8 dígitos)", InputType.TYPE_CLASS_NUMBER);
        cuentaDestino = campo("Cuenta destino (8 dígitos)", InputType.TYPE_CLASS_NUMBER);
        importeTransferencia = campo("Importe a transferir", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tarjeta.addView(cuentaOrigen);
        tarjeta.addView(cuentaDestino);
        tarjeta.addView(importeTransferencia);
        Button transferencia = boton("Transferir", false);
        transferencia.setOnClickListener(v -> { botonAccion = transferencia; transferir(); });
        tarjeta.addView(transferencia);
        estado = texto("Consulta movimientos o registra un depósito.", 14, MUTED, false);
        estado.setContentDescription("Estado de la operación");
        tarjeta.addView(estado);
        resultados = texto("", 14, TEXTO, false);
        resultados.setTextIsSelectable(true);
        resultados.setContentDescription("Movimientos de la cuenta");
        tarjeta.addView(resultados);
    }

    private void autenticar() {
        String nombre = usuario.getText().toString().trim();
        String clave = contrasena.getText().toString();
        ejecutar(() -> controlador.iniciarSesion(nombre, clave), resultado -> {
            if (Boolean.TRUE.equals(resultado)) mostrarPanel();
            else estado.setText("Credenciales incorrectas.");
            contrasena.setText("");
        });
    }

    private void consultar() {
        String numero = cuenta.getText().toString().trim();
        ejecutar(() -> controlador.traerMovimientos(numero), resultado -> {
            @SuppressWarnings("unchecked") List<Movimiento> lista = (List<Movimiento>) resultado;
            if (lista.isEmpty()) { resultados.setText("No hay movimientos para esta cuenta."); return; }
            StringBuilder contenido = new StringBuilder("N.º  ·  FECHA  ·  TIPO  ·  IMPORTE\n\n");
            for (Movimiento m : lista) contenido.append(m.numero()).append("  ·  ").append(m.fecha()).append("\n")
                    .append(m.tipo()).append(" · ").append(m.accion()).append(" · $ ").append(m.importe()).append("\n\n");
            resultados.setText(contenido.toString());
        });
    }

    private void depositar() {
        BigDecimal valor;
        try { valor = new BigDecimal(importe.getText().toString().trim().replace(',', '.')); }
        catch (NumberFormatException e) { estado.setText("Ingrese un importe decimal válido."); return; }
        if (valor.signum() <= 0 || valor.scale() > 2) { estado.setText("Use un importe positivo de hasta dos decimales."); return; }
        String numero = cuenta.getText().toString().trim();
        ejecutar(() -> controlador.registrarDeposito(numero, valor), resultado -> {
            if (Boolean.TRUE.equals(resultado)) { estado.setText("Depósito registrado."); consultar(); }
            else estado.setText("No se pudo registrar el depósito. Revise la cuenta.");
        });
    }

    private void transferir() {
        String origen = cuentaOrigen.getText().toString().trim();
        String destino = cuentaDestino.getText().toString().trim();
        BigDecimal valor;
        try { valor = new BigDecimal(importeTransferencia.getText().toString().trim().replace(',', '.')); }
        catch (NumberFormatException e) { estado.setText("Ingrese un importe decimal válido."); return; }
        if (!origen.matches("[0-9]{8}") || !destino.matches("[0-9]{8}") || origen.equals(destino)
                || valor.signum() <= 0 || valor.scale() > 2) {
            estado.setText("Use dos cuentas distintas e importe positivo de hasta dos decimales."); return;
        }
        ejecutar(() -> controlador.transferir(origen, destino, valor), resultado ->
                estado.setText(Boolean.TRUE.equals(resultado)
                        ? "Transferencia registrada: débito y crédito confirmados."
                        : "Transferencia rechazada. Revise cuentas, moneda, saldo e importe."));
    }

    private void ejecutar(Tarea tarea, java.util.function.Consumer<Object> alCompletar) {
        botonAccion.setEnabled(false);
        estado.setText("Conectando con EurekaBank…");
        tareas.execute(() -> {
            Object valor = null;
            Exception error = null;
            try { valor = tarea.ejecutar(); }
            catch (Exception e) { error = e; }
            Object resultado = valor;
            Exception fallo = error;
            principal.post(() -> {
                if (botonAccion != null) botonAccion.setEnabled(true);
                if (fallo != null) estado.setText("No se pudo completar la solicitud. Revise la conexión.");
                else { estado.setText("Operación completada."); alCompletar.accept(resultado); }
            });
        });
    }

    private void iniciarContenido() {
        raiz = new LinearLayout(this);
        raiz.setOrientation(LinearLayout.VERTICAL);
        raiz.setPadding(dp(20), dp(28), dp(20), dp(24));
        raiz.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(11, 16, 32), Color.rgb(16, 42, 68), Color.rgb(18, 73, 89)}));
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setContentDescription("Pantalla de EurekaBank");
        scroll.addView(raiz, new ScrollView.LayoutParams(-1, -2));
        setContentView(scroll);
    }

    private LinearLayout tarjeta() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(23), dp(24), dp(23), dp(24));
        panel.setBackground(redondeado(PANEL, 24));
        panel.setElevation(dp(10));
        return panel;
    }

    private EditText campo(String etiqueta, int tipo) {
        EditText edit = new EditText(this);
        edit.setSingleLine(true);
        edit.setHint(etiqueta);
        edit.setContentDescription(etiqueta);
        edit.setInputType(tipo);
        edit.setTextColor(TEXTO);
        edit.setHintTextColor(MUTED);
        edit.setTextSize(16);
        edit.setPadding(dp(15), dp(12), dp(15), dp(12));
        edit.setBackground(redondeado(CAMPO, 14));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(16);
        edit.setLayoutParams(p);
        return edit;
    }

    private Button boton(String titulo, boolean primario) {
        Button boton = new Button(this);
        boton.setText(titulo);
        boton.setAllCaps(false);
        boton.setTextSize(16);
        boton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        boton.setTextColor(primario ? FONDO : TURQUESA);
        boton.setMinHeight(dp(52));
        boton.setBackground(redondeado(primario ? TURQUESA : PANEL, 14));
        boton.setContentDescription(titulo);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(14);
        boton.setLayoutParams(p);
        return boton;
    }

    private TextView texto(String valor, int tamano, int color, boolean negrita) {
        TextView texto = new TextView(this);
        texto.setText(valor);
        texto.setTextSize(tamano);
        texto.setTextColor(color);
        if (negrita) texto.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        texto.setPadding(0, dp(4), 0, dp(4));
        return texto;
    }

    private GradientDrawable redondeado(int color, int radio) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radio));
        drawable.setStroke(dp(1), Color.rgb(45, 58, 85));
        return drawable;
    }

    private int dp(int valor) { return (int) (valor * getResources().getDisplayMetrics().density + 0.5f); }
    @Override protected void onDestroy() { tareas.shutdownNow(); super.onDestroy(); }
    @FunctionalInterface private interface Tarea { Object ejecutar() throws Exception; }
}
