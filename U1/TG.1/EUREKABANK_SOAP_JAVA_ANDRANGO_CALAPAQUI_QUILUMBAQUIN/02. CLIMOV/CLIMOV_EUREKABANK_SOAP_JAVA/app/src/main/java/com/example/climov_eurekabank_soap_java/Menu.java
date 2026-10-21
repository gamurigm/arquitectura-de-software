package com.example.climov_eurekabank_soap_java;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Menu extends AppCompatActivity {

    private Button btn_movimientos, btn_deposito;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer el diseño
        setContentView(R.layout.activity_menu);

        // Buscar las vistas después de setContentView
        btn_deposito = findViewById(R.id.btn_deposito);
        btn_movimientos = findViewById(R.id.btn_movimientos);

        // Configurar los listeners
        btn_deposito.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, EurekabankControlador.class);
            startActivity(intent);
        });

        btn_movimientos.setOnClickListener(v -> {
            Intent intent = new Intent(Menu.this, Depositos.class);
            startActivity(intent);
        });

        // Configurar EdgeToEdge
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}