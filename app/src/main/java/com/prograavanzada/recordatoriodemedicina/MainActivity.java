package com.prograavanzada.recordatoriodemedicina;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnAgregar;
    Button btnCerrarSesion;
    RecyclerView rvMedicinas;
    CardView cvEstadoVacio;

    MedicinaAdapter adaptador;
    AppDatabase baseDeDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- PEDIR PERMISO DE NOTIFICACIONES EN ANDROID 13+ ---
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // --- DISPARAR WORKMANAGER PARA EL ENTREGABLE ---
        androidx.work.OneTimeWorkRequest peticionNotificacion =
                new androidx.work.OneTimeWorkRequest.Builder(RecordatorioWorker.class).build();

        androidx.work.WorkManager.getInstance(this).enqueue(peticionNotificacion);

        // Vincular vistas
        btnAgregar = findViewById(R.id.btnAgregarMedicina);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        rvMedicinas = findViewById(R.id.rvMedicinas);
        cvEstadoVacio = findViewById(R.id.cvEstadoVacio);

        rvMedicinas.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new MedicinaAdapter();
        rvMedicinas.setAdapter(adaptador);

        // Reemplaza tu línea de baseDeDatos por esta:
        baseDeDatos = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "base_medicinas_v3")
                .fallbackToDestructiveMigration()
                .build();

        // Acción para agregar medicina
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AgregarMedicinaActivity.class);
            startActivity(intent);
        });

        // --- NUEVA LÓGICA: CERRAR SESIÓN ---
        btnCerrarSesion.setOnClickListener(v -> {
            // Borramos el token de sesión (isLoggedIn = false)
            SharedPreferences sharedPreferences = getSharedPreferences("PillMinderPrefs", MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply();

            // Redirigimos al LoginActivity y limpiamos el historial
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Configuración de los botones de la tarjeta (Editar/Borrar)
        adaptador.setOnItemClickListener(new MedicinaAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Medicina medicina) {
                Intent intent = new Intent(MainActivity.this, AgregarMedicinaActivity.class);
                intent.putExtra("id", medicina.id);
                intent.putExtra("nombre", medicina.nombre);
                intent.putExtra("cantidad", medicina.cantidad);
                intent.putExtra("inventario", medicina.inventarioTotal);
                intent.putExtra("fecha", medicina.fechaInicio);
                intent.putExtra("hora", medicina.horaAlarma);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Medicina medicina) {
                android.app.Dialog dialog = new android.app.Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_borrar);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

                TextView tvMensaje = dialog.findViewById(R.id.tvDialogMensaje);
                AppCompatButton btnCancelar = dialog.findViewById(R.id.btnDialogCancelar);
                AppCompatButton btnEliminar = dialog.findViewById(R.id.btnDialogEliminar);

                tvMensaje.setText("¿Estás seguro de que deseas eliminar " + medicina.nombre + " de tu lista?");
                btnCancelar.setOnClickListener(vDialog -> dialog.dismiss());
                btnEliminar.setOnClickListener(vDialog -> {
                    new Thread(() -> {
                        baseDeDatos.medicinaDao().eliminar(medicina);
                        runOnUiThread(() -> {
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), medicina.nombre + " eliminada", Snackbar.LENGTH_LONG);
                            snackbar.setTextColor(android.graphics.Color.BLACK);
                            View view = snackbar.getView();
                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                            params.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL;
                            params.setMargins(0, 0, 0, 180);
                            view.setLayoutParams(params);
                            view.setBackgroundResource(R.drawable.bg_rojo_redondeado);
                            view.setBackgroundTintList(null);
                            snackbar.show();
                            cargarMedicinasDesdeLaBase();
                            dialog.dismiss();
                        });
                    }).start();
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMedicinasDesdeLaBase();
    }

    private void cargarMedicinasDesdeLaBase() {
        new Thread(() -> {
            List<Medicina> lista = baseDeDatos.medicinaDao().obtenerTodas();
            runOnUiThread(() -> {
                if (lista.isEmpty()) {
                    cvEstadoVacio.setVisibility(View.VISIBLE);
                    rvMedicinas.setVisibility(View.GONE);
                } else {
                    cvEstadoVacio.setVisibility(View.GONE);
                    rvMedicinas.setVisibility(View.VISIBLE);
                    adaptador.setMedicinas(lista);
                }
            });
        }).start();



    }
}