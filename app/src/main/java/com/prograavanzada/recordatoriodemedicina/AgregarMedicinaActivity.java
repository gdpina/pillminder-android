package com.prograavanzada.recordatoriodemedicina;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AgregarMedicinaActivity extends AppCompatActivity {

    EditText etNombre, etCantidad, etInventario, etFrecuencia, etFecha, etHora, etDescripcion;
    Button btnGuardar;
    ImageView btnVolver;
    AppDatabase baseDeDatos;

    int idMedicinaEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_medicina);

        etNombre = findViewById(R.id.etNombre);
        etCantidad = findViewById(R.id.etCantidad);
        etInventario = findViewById(R.id.etInventario);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardar = findViewById(R.id.btnGuardarMedicina);
        btnVolver = findViewById(R.id.btnVolver);

        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        // --- LA CORRECCIÓN CLAVE ---
        // Aquí usamos el Singleton, no creamos una nueva conexión
        baseDeDatos = AppDatabase.getInstance(this);

        etFecha.setFocusable(false);
        etFecha.setClickable(true);
        etHora.setFocusable(false);
        etHora.setClickable(true);

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            idMedicinaEditar = intent.getIntExtra("id", -1);
            etNombre.setText(intent.getStringExtra("nombre"));
            etCantidad.setText(intent.getStringExtra("cantidad"));
            etInventario.setText(String.valueOf(intent.getIntExtra("inventario", 0)));
            etFrecuencia.setText(String.valueOf(intent.getIntExtra("frecuencia", 8)));
            etFecha.setText(intent.getStringExtra("fecha"));
            etHora.setText(intent.getStringExtra("hora"));
            etDescripcion.setText(intent.getStringExtra("descripcion"));
            btnGuardar.setText("ACTUALIZAR MEDICINA");
        }

        etNombre.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String nombreIngresado = etNombre.getText().toString().trim();
                String info = buscarInformacionMedica(nombreIngresado);
                if (!info.isEmpty()) {
                    mostrarAvisoMedico(nombreIngresado, info);
                }
            }
        });

        etFecha.setOnClickListener(v -> {
            MaterialDatePicker<Long> selectorFecha = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecciona el día")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            selectorFecha.addOnPositiveButtonClickListener(seleccion -> {
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                formato.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                etFecha.setText(formato.format(new Date(seleccion)));
            });
            selectorFecha.show(getSupportFragmentManager(), "FECHA_MODERNA");
        });

        etHora.setOnClickListener(v -> {
            int horaDefecto = 8;
            int minutoDefecto = 0;
            String horaGuardada = etHora.getText().toString();
            if (!horaGuardada.isEmpty()) {
                try {
                    SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm a", Locale.US);
                    Date fechaHora = formatoHora.parse(horaGuardada);
                    Calendar calendarioHora = Calendar.getInstance();
                    calendarioHora.setTime(fechaHora);
                    horaDefecto = calendarioHora.get(Calendar.HOUR_OF_DAY);
                    minutoDefecto = calendarioHora.get(Calendar.MINUTE);
                } catch (Exception e) { e.printStackTrace(); }
            }

            MaterialTimePicker selectorHora = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(horaDefecto)
                    .setMinute(minutoDefecto)
                    .setTitleText("Configura la primera alarma")
                    .build();

            selectorHora.addOnPositiveButtonClickListener(view -> {
                int hora = selectorHora.getHour();
                int minuto = selectorHora.getMinute();
                String amPm = (hora >= 12) ? "PM" : "AM";
                int hora12 = (hora > 12) ? hora - 12 : (hora == 0 ? 12 : hora);
                String horaElegida = String.format(Locale.getDefault(), "%02d:%02d %s", hora12, minuto, amPm);
                etHora.setText(horaElegida);
            });
            selectorHora.show(getSupportFragmentManager(), "HORA_MODERNA");
        });

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String cantidadStr = etCantidad.getText().toString().trim();
            String inventarioStr = etInventario.getText().toString().trim();
            String frecuenciaStr = etFrecuencia.getText().toString().trim();
            String fecha = etFecha.getText().toString().trim();
            String hora = etHora.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (nombre.isEmpty() || cantidadStr.isEmpty() || inventarioStr.isEmpty() || frecuenciaStr.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
                mostrarAvisoFlotante("Completa los campos obligatorios", R.drawable.bg_rojo_redondeado, "#FFFFFF");
                return;
            }

            int inventarioTotal = Integer.parseInt(inventarioStr);
            int frecuenciaHoras = Integer.parseInt(frecuenciaStr);

            new Thread(() -> {

                Medicina medicina = new Medicina(
                        nombre,
                        cantidadStr,
                        inventarioTotal,
                        inventarioTotal,
                        fecha,
                        hora,
                        frecuenciaHoras,
                        descripcion
                );

                if (idMedicinaEditar != -1) {

                    medicina.id = idMedicinaEditar;
                    baseDeDatos.medicinaDao().actualizar(medicina);

                } else {

                    baseDeDatos.medicinaDao().insertar(medicina);

                }

                programarAlarma(
                        nombre,
                        cantidadStr,
                        fecha,
                        hora,
                        frecuenciaHoras,
                        descripcion
                );

                runOnUiThread(() -> {

                    mostrarAvisoFlotante(
                            "¡Guardado exitosamente!",
                            R.drawable.bg_azul_redondeado,
                            "#FFFFFF"
                    );

                    new Handler().postDelayed(this::finish,1000);

                });

            }).start();
        });
    }

    private String buscarInformacionMedica(String nombreMedicamento) {
        String busqueda = nombreMedicamento.toLowerCase().trim();
        if (busqueda.contains("paracetamol") || busqueda.contains("acetaminofen")) return "Analgésico y antipirético. Sirve para aliviar dolor leve a moderado y reducir la fiebre. Tomar con agua.";
        if (busqueda.contains("ibuprofeno")) return "Antiinflamatorio. Recomendable tomar con comida.";
        if (busqueda.contains("amoxicilina")) return "Antibiótico. Es vital completar todo el tratamiento.";
        return "";
    }

    private void mostrarAvisoMedico(String nombre, String info) {
        new AlertDialog.Builder(this)
                .setTitle("Información de " + nombre)
                .setMessage(info + "\n\n¿Deseas agregar esta información a la descripción?")
                .setPositiveButton("Sí, agregar", (dialog, which) -> etDescripcion.setText(info))
                .setNegativeButton("No, gracias", null)
                .show();
    }

    private void programarAlarma(String nombre, String dosis, String fecha, String hora, int frecuenciaHoras, String descripcion) {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
            Date date = formato.parse(fecha + " " + hora);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            Intent intent = new Intent(this, AlarmaReceiver.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("dosis", dosis);
            intent.putExtra("frecuencia", frecuenciaHoras);
            intent.putExtra("descripcion", descripcion);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, nombre.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void mostrarAvisoFlotante(String mensaje, int diseñoFondo, String colorTexto) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.parseColor(colorTexto));
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, 0, 0, 180);
        view.setLayoutParams(params);
        view.setBackgroundResource(diseñoFondo);
        view.setBackgroundTintList(null);
        snackbar.show();
    }
}