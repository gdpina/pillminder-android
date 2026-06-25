package com.prograavanzada.recordatoriodemedicina;

import android.app.NotificationManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmaPantallaActivity extends AppCompatActivity {

    TextView tvNombre, tvDosis;
    androidx.appcompat.widget.AppCompatButton btnApagar;
    Ringtone tonoAlarma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        setContentView(R.layout.activity_alarma_pantalla);

        tvNombre = findViewById(R.id.tvAlarmaNombre);
        tvDosis = findViewById(R.id.tvAlarmaDosis);
        btnApagar = findViewById(R.id.btnApagarAlarma);

        String nombre = getIntent().getStringExtra("nombre");
        String dosis = getIntent().getStringExtra("dosis");
        // ¡Recibimos el ID de la notificación!
        int idNotificacion = getIntent().getIntExtra("id_notificacion", -1);

        tvNombre.setText(nombre);
        tvDosis.setText("Dosis: " + dosis);

        try {
            Uri sonidoUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            tonoAlarma = RingtoneManager.getRingtone(getApplicationContext(), sonidoUri);
            tonoAlarma.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnApagar.setOnClickListener(v -> {
            if (tonoAlarma != null && tonoAlarma.isPlaying()) {
                tonoAlarma.stop();
            }

            // --- ¡NUEVO! Borramos la notificación de la campanita ---
            if (idNotificacion != -1) {
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(idNotificacion);
            }

            new Thread(() -> {
                AppDatabase baseDeDatos = androidx.room.Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "base_medicinas_v2").build();

                java.util.List<Medicina> lista = baseDeDatos.medicinaDao().obtenerTodas();
                for (Medicina med : lista) {
                    if (med.nombre.equals(nombre)) {

                        int dosisTomada = Integer.parseInt(dosis);

                        med.inventarioTotal = med.inventarioTotal - dosisTomada; // Usa tu variable correcta aquí

                        if (med.inventarioTotal < 0) {
                            med.inventarioTotal = 0;
                        }

                        baseDeDatos.medicinaDao().actualizar(med);
                        break;
                    }
                }

                runOnUiThread(() -> finish());
            }).start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tonoAlarma != null && tonoAlarma.isPlaying()) {
            tonoAlarma.stop();
        }
    }
}