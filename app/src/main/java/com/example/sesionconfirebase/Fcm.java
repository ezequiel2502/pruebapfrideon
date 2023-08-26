package com.example.sesionconfirebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class Fcm extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "Nuevo";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String titulo = remoteMessage.getData().get("titulo");
        String detalle = remoteMessage.getData().get("detalle");


        Log.d("Tag", "Titulo recibido: " + titulo);
        Log.d("Tag", "Detalle recibido: " + detalle);



        // Crear una acción para el primer botón
        Intent actionIntent1 = new Intent(this, MainActivity2.class);
        actionIntent1.putExtra("BUTTON_CLICKED", "Botón 1");
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0, actionIntent1, PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(
                R.drawable.btn_acpt, "Botón 1", pendingIntent1).build();

        // Crear una acción para el segundo botón
        Intent actionIntent2 = new Intent(this, MainActivity2.class);
        actionIntent2.putExtra("BUTTON_CLICKED", "Botón 2");
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0, actionIntent2, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(
                R.drawable.btn_denied, "Botón 2", pendingIntent2).build();

        // Construir la notificación, con imagen y botones
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(detalle)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.doomer))
                .addAction(action1)
                .addAction(action2);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Nuevo", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(0, builder.build());
        }
    }
}
