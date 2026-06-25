package com.prograavanzada.recordatoriodemedicina;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.room.Room;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 1. Comprobamos que el celular realmente acaba de terminar de encender
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            // 2. Abrimos un hilo secundario porque vamos a leer la base de datos
            new Thread(() -> {
                AppDatabase baseDeDatos = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "base_medicinas_v2").build();

                // Obtenemos todas las medicinas guardadas
                List<Medicina> listaMedicinas = baseDeDatos.medicinaDao().obtenerTodas();
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);

                // 3. Recorremos la lista y reprogramamos una por una
                for (Medicina medicina : listaMedicinas) {
                    try {
                        String fechaHoraString = medicina.fechaInicio + " " + medicina.horaAlarma;
                        Date fechaAlarma = formato.parse(fechaHoraString);

                        Calendar calendario = Calendar.getInstance();
                        calendario.setTime(fechaAlarma);

                        // Si la hora de la pastilla ya pasó mientras el celular estaba apagado, la ignoramos
                        if (calendario.before(Calendar.getInstance())) {
                            continue;
                        }

                        Intent intentAlarma = new Intent(context, AlarmaReceiver.class);
                        intentAlarma.putExtra("nombre", medicina.nombre);
                        intentAlarma.putExtra("dosis", medicina.cantidad);

                        // Usamos el ID único de la medicina
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                context,
                                medicina.id,
                                intentAlarma,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        if (alarmManager != null) {
                            // Volvemos a "inyectar" la alarma en el sistema operativo
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendario.getTimeInMillis(), pendingIntent);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}