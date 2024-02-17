package com.example.sesionconfirebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Fcm extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "Rideon_ID";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Procesar el mensaje recibido
        if (remoteMessage.getNotification() != null) {
            // Aquí puedes manejar la notificación entrante, como mostrarla en una notificación en la barra de estado
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Crear el Intent para abrir ListadoNotificacionesActivity
            Intent acceptIntent = new Intent(this, ListadoNotificacionesActivity.class);
            acceptIntent.putExtra("ACTION", "Botón 1"); // Puedes pasar cualquier información adicional necesaria

// Envolver el Intent en un PendingIntent
            PendingIntent acceptPendingIntent = PendingIntent.getActivity(this, 0, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

// Construir la acción del botón de "Aceptar" utilizando el PendingIntent
            NotificationCompat.Action actionAccept = new NotificationCompat.Action.Builder(
                    R.drawable.btn_acpt, // Icono del botón
                    "Aceptar", // Texto del botón
                    acceptPendingIntent // PendingIntent que se activará cuando se presione el botón
            ).build();

// Construir la notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.doomer))
                    .addAction(actionAccept);

// Obtener el servicio de notificación
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Crear el canal de notificación para Android Oreo y versiones posteriores
                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Nuevo", NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }

                // Generar un ID único para la notificación
                int uniqueNotificationId = (int) System.currentTimeMillis();

                // Mostrar la notificación
                notificationManager.notify(uniqueNotificationId, builder.build());
            }
        }

    }


}

