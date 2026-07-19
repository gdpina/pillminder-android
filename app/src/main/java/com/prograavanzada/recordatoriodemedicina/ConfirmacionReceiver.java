package com.prograavanzada.recordatoriodemedicina;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ConfirmacionReceiver extends BroadcastReceiver {

    private static final String TAG = "CONFIRMACION";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "========== RECEIVER EJECUTADO ==========");

        String nombre = intent.getStringExtra(Constants.KEY_NOMBRE);

        int idNotificacion = intent.getIntExtra(
                Constants.KEY_ID_NOTIF,
                -1
        );

        if (nombre == null || nombre.trim().isEmpty()) {
            Log.e(TAG, "Nombre nulo.");
            return;
        }

        new Thread(() -> {

            try {
                AppDatabase db = AppDatabase.getInstance(context);
                Medicina medicina =
                        db.medicinaDao().obtenerPorNombre(nombre);
                if (medicina == null) {
                    Log.e(TAG, "No existe la medicina.");
                    return;
                }

                int dosis;
                try {
                    dosis = Integer.parseInt(medicina.cantidad);
                } catch (Exception e) {
                    dosis = 1;
                }

                if (medicina.inventarioTotal >= dosis) {
                    db.medicinaDao().restarStockPorNombre(
                            nombre,
                            dosis
                    );

                    db.medicinaDao().actualizarFechaToma(
                            nombre,
                            System.currentTimeMillis()
                    );

                    Log.d(TAG, "Inventario actualizado.");
                }

                if (idNotificacion != -1) {
                    NotificationManager manager =
                            (NotificationManager)
                                    context.getSystemService(
                                            Context.NOTIFICATION_SERVICE
                                    );

                    if (manager != null) {
                        manager.cancel(idNotificacion);
                    }

                }

            } catch (Exception e) {
                Log.e(TAG,
                        "Error",
                        e);
            }

        }).start();

    }

}