package com.prograavanzada.recordatoriodemedicina;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class RecordatorioWorker extends Worker {

    public RecordatorioWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        String canalId = "canal_pastillas";
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear el canal (Requisito API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    canalId,
                    "Recordatorios Diarios",
                    NotificationManager.IMPORTANCE_HIGH
            );
            canal.setDescription("Canal para avisos de medicación");
            manager.createNotificationChannel(canal);
        }

        // Crear acción para abrir la app al tocar la notificación
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Construir la notificación con diseño expandido (BigTextStyle)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(android.R.drawable.ic_popup_reminder) // Cambiamos a un ícono de recordatorio
                .setContentTitle("📋 Resumen Diario PillMinder")
                .setContentText("Tienes medicinas programadas para hoy.") // Texto corto si está colapsada
                .setStyle(new NotificationCompat.BigTextStyle()
                        // Aquí va el texto largo que hace que el recuadro se vea grande
                        .bigText("¡Buenos días! Tienes medicinas programadas para hoy.\n\nRecuerda que mantener tu tratamiento al día es fundamental para cuidar tu salud. Toca esta notificación para abrir tu farmacia y ver tu rutina completa."))
                .setPriority(NotificationCompat.PRIORITY_MAX) // MAX para que salte en pantalla sí o sí
                .setContentIntent(pendingIntent) // Vinculamos el clic a la app
                .setAutoCancel(true); // Desaparece al tocarla

        // Lanzar la notificación
        manager.notify(1, builder.build());

        return Result.success();
    }
}