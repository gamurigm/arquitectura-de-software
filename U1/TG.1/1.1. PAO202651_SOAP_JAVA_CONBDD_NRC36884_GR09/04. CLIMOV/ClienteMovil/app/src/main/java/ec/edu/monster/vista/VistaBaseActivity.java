package ec.edu.monster.vista;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

abstract class VistaBaseActivity extends Activity {
    private LinearLayout contenido;
    private final int menta = Color.rgb(8, 127, 123);
    private final int texto = Color.rgb(23, 50, 77);

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        contenido = new LinearLayout(this);
        contenido.setOrientation(LinearLayout.VERTICAL);
        contenido.setPadding(dp(24), dp(30), dp(24), dp(28));
        contenido.setBackgroundColor(Color.rgb(243, 247, 250));
        scroll.addView(contenido);
        setContentView(scroll);
    }

    final void encabezado(String titulo, String detalle) {
        TextView marca = new TextView(this);
        marca.setText("MONSTER  ·  BANCA"); marca.setTextColor(menta);
        marca.setTextSize(12); marca.setTypeface(null, Typeface.BOLD); marca.setLetterSpacing(.12f);
        contenido.addView(marca, margen(dp(10)));
        TextView nombre = new TextView(this);
        nombre.setText("EurekaBank"); nombre.setTextColor(texto); nombre.setTextSize(22);
        nombre.setTypeface(null, Typeface.BOLD); contenido.addView(nombre, margen(dp(32)));
        TextView h = new TextView(this);
        h.setText(titulo); h.setTextColor(texto); h.setTextSize(27); h.setTypeface(null, Typeface.BOLD);
        contenido.addView(h, margen(dp(8)));
        TextView sub = new TextView(this);
        sub.setText(detalle); sub.setTextColor(Color.rgb(102, 123, 142)); sub.setTextSize(15);
        contenido.addView(sub, margen(dp(22)));
    }

    final EditText campo(String etiqueta, int tipo) {
        TextView label = new TextView(this); label.setText(etiqueta); label.setTextColor(texto);
        label.setTextSize(14); label.setTypeface(null, Typeface.BOLD); contenido.addView(label, margen(dp(7)));
        EditText field = new EditText(this); field.setHint(etiqueta); field.setSingleLine(true);
        field.setTextColor(texto); field.setHintTextColor(Color.rgb(102, 123, 142));
        field.setTextSize(16); field.setInputType(tipo); field.setPadding(dp(13), dp(6), dp(13), dp(6));
        field.setBackground(fondo(Color.WHITE, Color.rgb(185, 202, 215)));
        field.setMinHeight(dp(52)); contenido.addView(field, margen(dp(6)));
        return field;
    }

    final Spinner selectorCuentas() {
        String[] etiquetas = getIntent().getStringArrayExtra(MenuActivity.ETIQUETAS_CUENTAS);
        if (etiquetas == null || etiquetas.length == 0) etiquetas = new String[]{"No hay cuentas activas asignadas"};
        Spinner selector = new Spinner(this);
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, etiquetas);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selector.setAdapter(adaptador);
        selector.setContentDescription("Cuenta bancaria activa");
        contenido.addView(selector, margen(dp(8)));
        return selector;
    }

    final String cuentaSeleccionada(Spinner selector) {
        String[] cuentas = getIntent().getStringArrayExtra(MenuActivity.CUENTAS);
        int indice = selector.getSelectedItemPosition();
        return cuentas != null && indice >= 0 && indice < cuentas.length ? cuentas[indice] : "";
    }

    final Button accion(String etiqueta, Runnable tarea) {
        Button button = new Button(this); button.setText(etiqueta); button.setTextColor(Color.WHITE);
        button.setTextSize(15); button.setTypeface(null, Typeface.BOLD); button.setAllCaps(false);
        button.setBackground(fondo(menta, menta)); button.setMinHeight(dp(52));
        button.setOnClickListener(v -> tarea.run()); contenido.addView(button, margen(dp(18)));
        return button;
    }

    final Button botonSecundario(String etiqueta, Runnable tarea) {
        Button button = accion(etiqueta, tarea); button.setTextColor(texto);
        button.setBackground(fondo(Color.rgb(234, 243, 244), Color.rgb(217, 228, 236)));
        return button;
    }

    final TextView salida() {
        TextView view = new TextView(this); view.setTextColor(texto); view.setTextSize(15);
        view.setLineSpacing(dp(5), 1f); view.setPadding(dp(14), dp(12), dp(14), dp(12));
        view.setBackground(fondo(Color.WHITE, Color.rgb(217, 228, 236)));
        contenido.addView(view, margen(dp(15))); return view;
    }

    final void mensaje(String valor) { Toast.makeText(this, valor, Toast.LENGTH_LONG).show(); }
    final void esperar(Button button) { button.setEnabled(false); button.setText("Conectando…"); }
    final void listo(Button button, String texto) { button.setEnabled(true); button.setText(texto); }
    final int dp(int valor) { return Math.round(valor * getResources().getDisplayMetrics().density); }

    private LinearLayout.LayoutParams margen(int abajo) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.bottomMargin = abajo; return p;
    }

    private GradientDrawable fondo(int color, int borde) {
        GradientDrawable shape = new GradientDrawable(); shape.setColor(color); shape.setCornerRadius(dp(12));
        shape.setStroke(dp(1), borde); return shape;
    }
}
