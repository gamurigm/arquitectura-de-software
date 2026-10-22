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
import android.widget.TextView;
import android.widget.Toast;

abstract class VistaBaseActivity extends Activity {
    private LinearLayout contenido;
    private final int menta = Color.rgb(85, 196, 187);
    private final int texto = Color.rgb(237, 243, 255);

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        contenido = new LinearLayout(this);
        contenido.setOrientation(LinearLayout.VERTICAL);
        contenido.setPadding(dp(24), dp(30), dp(24), dp(28));
        contenido.setBackgroundColor(Color.rgb(7, 19, 35));
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
        sub.setText(detalle); sub.setTextColor(Color.rgb(169, 186, 208)); sub.setTextSize(15);
        contenido.addView(sub, margen(dp(22)));
    }

    final EditText campo(String etiqueta, int tipo) {
        TextView label = new TextView(this); label.setText(etiqueta); label.setTextColor(texto);
        label.setTextSize(14); label.setTypeface(null, Typeface.BOLD); contenido.addView(label, margen(dp(7)));
        EditText field = new EditText(this); field.setHint(etiqueta); field.setSingleLine(true);
        field.setTextColor(texto); field.setHintTextColor(Color.rgb(155, 171, 192));
        field.setTextSize(16); field.setInputType(tipo); field.setPadding(dp(13), dp(6), dp(13), dp(6));
        field.setBackground(fondo(Color.rgb(9, 23, 40), Color.rgb(52, 75, 101)));
        field.setMinHeight(dp(52)); contenido.addView(field, margen(dp(6)));
        return field;
    }

    final Button accion(String etiqueta, Runnable tarea) {
        Button button = new Button(this); button.setText(etiqueta); button.setTextColor(Color.rgb(6, 32, 38));
        button.setTextSize(15); button.setTypeface(null, Typeface.BOLD); button.setAllCaps(false);
        button.setBackground(fondo(menta, menta)); button.setMinHeight(dp(52));
        button.setOnClickListener(v -> tarea.run()); contenido.addView(button, margen(dp(18)));
        return button;
    }

    final Button botonSecundario(String etiqueta, Runnable tarea) {
        Button button = accion(etiqueta, tarea); button.setTextColor(texto);
        button.setBackground(fondo(Color.rgb(17, 31, 50), Color.rgb(52, 75, 101)));
        return button;
    }

    final TextView salida() {
        TextView view = new TextView(this); view.setTextColor(texto); view.setTextSize(15);
        view.setLineSpacing(dp(5), 1f); view.setPadding(dp(14), dp(12), dp(14), dp(12));
        view.setBackground(fondo(Color.rgb(17, 31, 50), Color.rgb(38, 59, 83)));
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
