package com.prograavanzada.recordatoriodemedicina;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AgregarMedicinaActivity extends AppCompatActivity {

    EditText etNombre, etCantidad, etInventario, etFrecuencia, etFecha, etHora, etDescripcion;
    android.widget.Button btnGuardar;
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

        baseDeDatos = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "base_medicinas_v3").fallbackToDestructiveMigration().build();

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

        // --- ¡LA MAGIA DEL DICCIONARIO LOCAL AQUÍ! ---
        // Esto detecta cuando el usuario termina de escribir el nombre y toca otro campo
        // --- AVISO INTELIGENTE AL DETECTAR MEDICINA ---
        etNombre.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // Cuando el usuario termina de escribir y sale del campo
                String nombreIngresado = etNombre.getText().toString().trim();
                String info = buscarInformacionMedica(nombreIngresado);

                if (!info.isEmpty()) {
                    mostrarAvisoMedico(nombreIngresado, info);
                }
            }
        });
        // ----------------------------------------------

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                mostrarAvisoFlotante("Por favor, completa todos los campos obligatorios", R.drawable.bg_rojo_redondeado, "#FFFFFF");
                return;
            }

            int dosis = Integer.parseInt(cantidadStr);
            int inventarioTotal = Integer.parseInt(inventarioStr);
            int frecuenciaHoras = Integer.parseInt(frecuenciaStr);

            if (dosis > inventarioTotal) {
                etCantidad.setError("La dosis no puede ser mayor");
                mostrarAvisoFlotante("Error: La dosis supera a la caja", R.drawable.bg_rojo_redondeado, "#FFFFFF");
                return;
            }

            try {
                String fechaHoraString = fecha + " " + hora;
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
                Date fechaAlarma = formato.parse(fechaHoraString);

                Calendar calendarioAlarma = Calendar.getInstance();
                calendarioAlarma.setTime(fechaAlarma);

                Calendar ahora = Calendar.getInstance();
                ahora.add(Calendar.MINUTE, -1);

                if (calendarioAlarma.before(ahora)) {
                    mostrarAvisoFlotante("Error: Esa fecha y hora ya pasaron", R.drawable.bg_rojo_redondeado, "#FFFFFF");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Thread(() -> {
                Medicina medicina = new Medicina(nombre, cantidadStr, inventarioTotal, fecha, hora, frecuenciaHoras, descripcion);
                String mensajeExito;

                if (idMedicinaEditar != -1) {
                    medicina.id = idMedicinaEditar;
                    baseDeDatos.medicinaDao().actualizar(medicina);
                    mensajeExito = "¡Actualizado exitosamente!";
                } else {
                    baseDeDatos.medicinaDao().insertar(medicina);
                    mensajeExito = "¡Guardado exitosamente!";
                }

                programarAlarma(nombre, cantidadStr, fecha, hora, frecuenciaHoras, descripcion);

                runOnUiThread(() -> {
                    mostrarAvisoFlotante(mensajeExito, R.drawable.bg_azul_redondeado, "#FFFFFF");
                    new android.os.Handler().postDelayed(() -> finish(), 1000);
                });
            }).start();
        });
    }

    // --- MINI DICCIONARIO MÉDICO (Puedes agregar más medicinas aquí) ---
    private String buscarInformacionMedica(String nombreMedicamento) {
        String busqueda = nombreMedicamento.toLowerCase().trim();

        if (busqueda.contains("paracetamol") || busqueda.contains("acetaminofen")) {
            return "Analgésico y antipirético. Sirve para aliviar dolor leve a moderado y reducir la fiebre. Tomar con agua.";
        } else if (busqueda.contains("ibuprofeno") || busqueda.contains("advil")) {
            return "Antiinflamatorio. Usado para reducir fiebre, dolores musculares e inflamación. Recomendable tomar con comida.";
        } else if (busqueda.contains("amoxicilina")) {
            return "Antibiótico. Usado para tratar infecciones bacterianas. Es vital completar todo el tratamiento.";
        } else if (busqueda.contains("loratadina") || busqueda.contains("cetirizina")) {
            return "Antihistamínico. Alivia los síntomas de alergias como estornudos, picazón y ojos llorosos. No causa sueño.";
        } else if (busqueda.contains("omeprazol")) {
            return "Protector gástrico. Reduce la acidez estomacal. Tomar en ayunas 30 minutos antes del desayuno.";
        } else if (busqueda.contains("aspirina")) {
            return "Analgésico y antiinflamatorio. A veces usado para problemas cardiovasculares bajo receta médica.";
        } else if (busqueda.contains("metformina")) {
            return "Antidiabético. Ayuda a controlar los niveles de azúcar en la sangre en pacientes con diabetes tipo 2.";
        } else {
            return ""; // Si no la reconoce, lo deja vacío para que el usuario escriba lo que quiera
        }
    }

    private void programarAlarma(String nombre, String dosis, String fecha, String hora, int frecuenciaHoras, String descripcion) {
        try {
            String fechaHoraString = fecha + " " + hora;
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
            Date fechaAlarma = formato.parse(fechaHoraString);

            Calendar calendario = Calendar.getInstance();
            calendario.setTime(fechaAlarma);

            Calendar ahora = Calendar.getInstance();
            ahora.add(Calendar.MINUTE, -1);

            if (calendario.before(ahora)) {
                runOnUiThread(() -> android.widget.Toast.makeText(this, "⚠️ La hora ya pasó", android.widget.Toast.LENGTH_LONG).show());
                return;
            }

            Intent intent = new Intent(this, AlarmaReceiver.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("dosis", dosis);
            intent.putExtra("frecuencia", frecuenciaHoras);
            intent.putExtra("descripcion", descripcion);

            int codigoUnico = nombre.hashCode();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, codigoUnico, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        runOnUiThread(() -> android.widget.Toast.makeText(this, "❌ Dale permisos de Alarmas en Configuración.", android.widget.Toast.LENGTH_LONG).show());
                        return;
                    }
                }
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), pendingIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> android.widget.Toast.makeText(this, "Error: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show());
        }
    }

    private void mostrarAvisoFlotante(String mensaje, int diseñoFondo, String colorTexto) {
        com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(findViewById(android.R.id.content), mensaje, com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
        snackbar.setTextColor(android.graphics.Color.parseColor(colorTexto));

        android.view.View view = snackbar.getView();
        android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.CENTER_HORIZONTAL;
        params.width = android.widget.FrameLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(0, 0, 0, 180);
        view.setLayoutParams(params);

        view.setBackgroundResource(diseñoFondo);
        view.setBackgroundTintList(null);

        snackbar.show();
    }

    private void mostrarAvisoMedico(String nombre, String info) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Información de " + nombre)
                .setMessage(info + "\n\n¿Deseas agregar esta información a la descripción?")
                .setPositiveButton("Sí, agregar", (dialog, which) -> {
                    etDescripcion.setText(info);
                })
                .setNegativeButton("No, gracias", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }
}