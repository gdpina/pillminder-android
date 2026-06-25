package com.prograavanzada.recordatoriodemedicina;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

public class AlarmaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String nombreMedicina = intent.getStringExtra("nombre");
        String dosis = intent.getStringExtra("dosis");
        int frecuencia = intent.getIntExtra("frecuencia", 8); // Nuevo: frecuencia en horas
        String descripcion = intent.getStringExtra("descripcion"); // Nuevo: nota

        // 1. Despertar el dispositivo
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE,
                    "PillMinder::AlarmaDespierta"
            );
            wakeLock.acquire(5000);
        }

        // 2. Programar la SIGUIENTE alarma antes de que el usuario haga nada
        programarSiguienteAlarma(context, nombreMedicina, dosis, frecuencia, descripcion);

        // 3. Notificación y Pantalla Completa
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "canal_medicinas_extremo";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Alarmas a Pantalla Completa", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Despierta la pantalla para tomar pastillas");
            notificationManager.createNotificationChannel(channel);
        }

        int idNotificacion = nombreMedicina != null ? nombreMedicina.hashCode() : (int) System.currentTimeMillis();

        Intent intentPantallaCompleta = new Intent(context, AlarmaPantallaActivity.class);
        intentPantallaCompleta.putExtra("nombre", nombreMedicina);
        intentPantallaCompleta.putExtra("dosis", dosis);
        intentPantallaCompleta.putExtra("descripcion", descripcion);
        intentPantallaCompleta.putExtra("id_notificacion", idNotificacion);
        intentPantallaCompleta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntentPantalla = PendingIntent.getActivity(context, idNotificacion, intentPantallaCompleta, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("¡Hora de tu medicina: " + nombreMedicina + "!")
                .setContentText("Dosis: " + dosis + ". " + descripcion)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntentPantalla, true)
                .setAutoCancel(true);

        notificationManager.notify(idNotificacion, builder.build());

        try {
            context.startActivity(intentPantallaCompleta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void programarSiguienteAlarma(Context context, String nombre, String dosis, int frecuencia, String descripcion) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Calcular tiempo: ahora + frecuencia de horas
        Calendar proxima = Calendar.getInstance();
        proxima.add(Calendar.HOUR_OF_DAY, frecuencia);

        Intent intent = new Intent(context, AlarmaReceiver.class);
        intent.putExtra("nombre", nombre);
        intent.putExtra("dosis", dosis);
        intent.putExtra("frecuencia", frecuencia);
        intent.putExtra("descripcion", descripcion);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                nombre.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, proxima.getTimeInMillis(), pendingIntent);
        }
    }
}