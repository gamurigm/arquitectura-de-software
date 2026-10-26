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
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ec.edu.monster.controlador.ControladorCliente;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Cuenta;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ActividadPrincipal extends Activity {
    private static final int FONDO = Color.rgb(243, 247, 250);
    private static final int PANEL = Color.WHITE;
    private static final int CAMPO = Color.WHITE;
    private static final int TEXTO = Color.rgb(23, 50, 77);
    private static final int MUTED = Color.rgb(102, 123, 142);
    private static final int TURQUESA = Color.rgb(8, 127, 123);
    private final ControladorCliente controlador = new ControladorCliente();
    private final ExecutorService tareas = Executors.newSingleThreadExecutor();
    private final Handler principal = new Handler(Looper.getMainLooper());
    private LinearLayout raiz;
    private EditText usuario;
    private EditText contrasena;
    private Spinner cuenta;
    private EditText importe;
    private Spinner cuentaOrigen;
    private Spinner cuentaDestino;
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
        tarjeta.addView(texto("MIS CUENTAS · SALDO DISPONIBLE", 12, TURQUESA, true));
        LinearLayout listaCuentas = new LinearLayout(this);
        listaCuentas.setOrientation(LinearLayout.VERTICAL);
        listaCuentas.setTag("lista-cuentas");
        tarjeta.addView(listaCuentas);
        cuenta = new Spinner(this);
        importe = campo("Importe del depósito", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        tarjeta.addView(cuenta);
        tarjeta.addView(importe);
        Button movimientos = boton("Consultar movimientos", true);
        movimientos.setOnClickListener(v -> { botonAccion = (Button) v; consultar(); });
        tarjeta.addView(movimientos);
        Button deposito = boton("Registrar depósito", false);
        deposito.setOnClickListener(v -> { botonAccion = (Button) v; depositar(); });
        tarjeta.addView(deposito);
        cuentaOrigen = new Spinner(this);
        cuentaDestino = new Spinner(this);
        importeTransferencia = campo("Importe a transferir", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        tarjeta.addView(texto("CUENTA DE ORIGEN", 12, MUTED, true));
        tarjeta.addView(cuentaOrigen);
        tarjeta.addView(texto("CUENTA DE DESTINO · MISMA MONEDA", 12, MUTED, true));
        tarjeta.addView(cuentaDestino);
        importeTransferencia.setHint("Importe en la moneda seleccionada");
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
            if (Boolean.TRUE.equals(resultado)) { mostrarPanel(); llenarCuentas(); }
            else estado.setText("Credenciales incorrectas.");
            contrasena.setText("");
        });
    }

    private void consultar() {
        String numero = cuentaSeleccionada(cuenta);
        if (numero.isBlank()) { estado.setText("No hay cuentas activas asignadas."); return; }
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
        String numero = cuentaSeleccionada(cuenta);
        if (numero.isBlank()) { estado.setText("No hay cuentas activas asignadas."); return; }
        ejecutar(() -> controlador.registrarDeposito(numero, valor), resultado -> {
            if (Boolean.TRUE.equals(resultado)) { estado.setText("Depósito registrado."); consultar(); }
            else estado.setText("No se pudo registrar el depósito. Revise la cuenta.");
        });
    }

    private void transferir() {
        String origen = cuentaSeleccionada(cuentaOrigen);
        String destino = cuentaSeleccionada(cuentaDestino);
        BigDecimal valor;
        try { valor = new BigDecimal(importeTransferencia.getText().toString().trim().replace(',', '.')); }
        catch (NumberFormatException e) { estado.setText("Ingrese un importe decimal válido."); return; }
        if (origen.isBlank() || destino.isBlank() || origen.equals(destino)
                || valor.signum() <= 0 || valor.scale() > 2) {
            estado.setText("Seleccione cuentas distintas en la misma moneda e importe positivo."); return;
        }
        ejecutar(() -> controlador.transferir(origen, destino, valor), resultado ->
                estado.setText(Boolean.TRUE.equals(resultado)
                        ? "Transferencia registrada: débito y crédito confirmados."
                        : "Transferencia rechazada. Revise cuentas, moneda, saldo e importe."));
    }

    private void llenarCuentas() {
        var cuentas = controlador.cuentas();
        LinearLayout listaCuentas = (LinearLayout) raiz.findViewWithTag("lista-cuentas");
        if (listaCuentas != null) {
            listaCuentas.removeAllViews();
            for (Cuenta c : cuentas) {
                LinearLayout fila = new LinearLayout(this);
                fila.setOrientation(LinearLayout.VERTICAL);
                fila.setPadding(dp(14), dp(12), dp(14), dp(12));
                fila.setBackground(redondeado(PANEL, 14));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
                p.topMargin = dp(8);
                fila.setLayoutParams(p);
                fila.addView(texto(c.numero() + " · " + c.nombreMoneda(), 14, TEXTO, true));
                fila.addView(texto("Saldo disponible  " + c.simbolo() + " " + c.saldoFormateado(), 20, TURQUESA, true));
                listaCuentas.addView(fila);
            }
        }
        var adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cuentas);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuenta.setAdapter(adaptador);
        var origen = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cuentas);
        origen.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuentaOrigen.setAdapter(origen);
        actualizarDestinos();
        cuentaOrigen.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { actualizarDestinos(); }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { cuentaDestino.setAdapter(null); }
        });
        if (cuentas.isEmpty()) estado.setText("No hay cuentas activas asignadas.");
    }

    private void actualizarDestinos() {
        String origen = cuentaSeleccionada(cuentaOrigen);
        Cuenta cuentaOrigenSeleccionada = controlador.cuentas().stream()
                .filter(c -> c.numero().equals(origen)).findFirst().orElse(null);
        String destinoActual = cuentaSeleccionada(cuentaDestino);
        var destinos = new java.util.ArrayList<Cuenta>();
        controlador.cuentas().stream().filter(c -> cuentaOrigenSeleccionada != null
                && !c.numero().equals(origen) && c.moneda().equals(cuentaOrigenSeleccionada.moneda()))
                .forEach(destinos::add);
        var adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, destinos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cuentaDestino.setAdapter(adaptador);
        for (int i = 0; i < destinos.size(); i++) if (destinos.get(i).numero().equals(destinoActual)) cuentaDestino.setSelection(i);
    }

    private static String cuentaSeleccionada(Spinner selector) {
        return selector.getSelectedItem() instanceof Cuenta cuenta ? cuenta.numero() : "";
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
                if (fallo != null) {
                    String detalle = fallo.getMessage();
                    estado.setText(detalle != null && detalle.startsWith("El servidor respondió HTTP ")
                            ? detalle
                            : "No se pudo completar la solicitud. Revise la conexión.");
                }
                else { estado.setText("Operación completada."); alCompletar.accept(resultado); }
            });
        });
    }

    private void iniciarContenido() {
        raiz = new LinearLayout(this);
        raiz.setOrientation(LinearLayout.VERTICAL);
        raiz.setPadding(dp(20), dp(28), dp(20), dp(24));
        raiz.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(243, 247, 250), Color.rgb(232, 241, 243), Color.rgb(243, 247, 250)}));
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
