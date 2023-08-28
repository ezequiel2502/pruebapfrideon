package com.example.sesionconfirebase;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("ACTION");

        if ("Botón 1".equals(action)) {

            // Aquí puedes enviar un broadcast específico para capturar la acción en la SingleEventoPublicoActivity
            Intent broadcastIntent = new Intent("com.example.sesionconfirebase.ACTION_POSTULAR");
            context.sendBroadcast(broadcastIntent);
        }  else if ("Botón 2".equals(action)) {
            // Aquí envías un broadcast específico para capturar la acción del Botón 2 en la SingleEventoPublicoActivity
//            Intent broadcastIntent = new Intent("com.example.sesionconfirebase.ACTION_DENEGAR_POSTULACION");
//            context.sendBroadcast(broadcastIntent);
        }
    }


}
