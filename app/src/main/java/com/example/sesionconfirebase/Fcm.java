package com.example.sesionconfirebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class Fcm extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "Nuevo";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String titulo = remoteMessage.getData().get("titulo");
        String detalle = remoteMessage.getData().get("detalle");
        String tipoNotificacion = remoteMessage.getData().get("tipo");

        if ("creador_evento".equals(tipoNotificacion)) {
            // Crear una acción para el primer botón
            Intent actionIntent1 = new Intent(this, NotificationActionReceiver.class);
            actionIntent1.putExtra("ACTION", "Botón 1");
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 0, actionIntent1, PendingIntent.FLAG_MUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(
                    R.drawable.btn_acpt, "Aceptar", pendingIntent1).build();

            // Crear una acción para el segundo botón
            Intent actionIntent2 = new Intent(this, NotificationActionReceiver.class);
            actionIntent2.putExtra("ACTION", "Botón 2");
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 0, actionIntent2, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Action action2 = new NotificationCompat.Action.Builder(
                    R.drawable.btn_denied, "Denegar", pendingIntent2).build();

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
                // Generar un ID único para la notificación
                Random random = new Random();
                int uniqueNotificationId = random.nextInt(10000);

                // Notificar utilizando el ID único
                notificationManager.notify(uniqueNotificationId, builder.build());
            }

        } else if ("postulante_evento".equals(tipoNotificacion)) {

            // Notificación para el postulante de evento
            Intent intent = new Intent(this, ListaEventoPostulados.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(titulo)
                    .setContentText(detalle)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);  // Agrega el PendingIntent

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Nuevo", NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }
                // Generar un ID único para la notificación
                Random random = new Random();
                int uniqueNotificationId = random.nextInt(10000);

                // Notificar utilizando el ID único
                notificationManager.notify(uniqueNotificationId, builder.build());
            }


        }


    }
}