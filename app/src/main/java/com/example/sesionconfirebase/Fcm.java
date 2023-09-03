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

    private static final String CHANNEL_ID = "Nuevo";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        String titulo = remoteMessage.getData().get("titulo");
        String detalle = remoteMessage.getData().get("detalle");
        String tipoNotificacion = remoteMessage.getData().get("tipo");
        String idEvento = remoteMessage.getData().get("idEvento");
        String postulanteId = remoteMessage.getData().get("postulanteId");
        String nombreEvento = remoteMessage.getData().get("nombreEvento");
        String tokenCreador = remoteMessage.getData().get("tokenCreador");
        String tokenPostulante = remoteMessage.getData().get("tokenPostulante");

        if ("creador_evento".equals(tipoNotificacion)) {


            // Crear una acción para el primer botón
            Intent actionIntent1 = new Intent(this, NotificationActionReceiver.class);
            actionIntent1.putExtra("idEvento", idEvento);
            actionIntent1.putExtra("nombreEvento", nombreEvento);
            actionIntent1.putExtra("postulanteId", postulanteId);
            actionIntent1.putExtra("tokenCreador", tokenCreador);
            actionIntent1.putExtra("tokenPostulante", tokenPostulante);

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
            // Llamar al método buscarPrimerNoAceptado()
            //buscarPrimerNoAceptado();
            //buscarNoAceptadoPorEventoYUsuario(idEvento,postulanteId);
            //notificarPostulanteEvento(nombreEvento,tokenPostulante);

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

//    private void notificarPostulanteEvento( String nombreEvento, String tokenPostulante) {
//
//        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
//        JSONObject json = new JSONObject();
//
//        try {
//            JSONObject notificacion = new JSONObject();
//            notificacion.put("titulo", "Aceptaron tu postulacion a : ");
//            notificacion.put("detalle", nombreEvento);
//            notificacion.put("tipo", "postulante_evento");
//
//            json.put("to", "fQnMIcygTWS_SIXnn2wdbd:APA91bHf_edzlaZ847PJzCd_efc27OCFT0Hl9iRfPaX8oLHRhI-YGnNPNDACUWuojBwcTp_Nal3QzohbWKb8NG3Co3YD6ooB92Utqm6N1wLa43S_wKVEN9dVbS3d-WTVTTGdwrS0iecM");
//            json.put("data", notificacion); // Cambio de "data" a "notification"
//
//
//            // URL que se utilizará para enviar la solicitud POST al servidor de FCM
//            String URL = "https://fcm.googleapis.com/fcm/send";
//
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, null, null) {
//                @Override
//                public Map<String, String> getHeaders() {
//                    Map<String, String> header = new HashMap<>();
//                    header.put("Content-Type", "application/json");
//                    header.put("Authorization", "Bearer AAAA2KZHDiM:APA91bHxMVQ1jcd7sRVOqoP9ffdSEFiBnVr_iFKOL0kd_X71Arrc3lSi8is74MYUB6Iyg_1DmbvJK42Ejk-6N-i9g-yDeVjncE09U8GUOVx9YpDWjpDywU_wLXQvCO0ZERz5qZc9_zqM");
//                    return header;
//                }
//            };
//            myrequest.add(request);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


    private void buscarNoAceptadoPorEventoYUsuario(String idEvento, String userId) {
        DatabaseReference prePostulacionesRef = FirebaseDatabase.getInstance().getReference().child("Pre-Postulaciones");

        DatabaseReference eventoRef = prePostulacionesRef.child(idEvento);
        DatabaseReference usuarioRef = eventoRef.child(userId);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PrePostulacion prePostulacion = dataSnapshot.getValue(PrePostulacion.class);

                if (prePostulacion != null && !prePostulacion.getAceptado()) {
                    String tokenFcmPostulante = prePostulacion.getTokenFcmPostulante();

                    usuarioRef.child("aceptado").setValue(true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        postularCandidato2(idEvento, userId, tokenFcmPostulante);
                                    } else {
                                        // Manejar el error en la actualización
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });
    }


    private void buscarPrimerNoAceptado() {
        DatabaseReference prePostulacionesRef = FirebaseDatabase.getInstance().getReference().child("Pre-Postulaciones");

        prePostulacionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventoSnapshot : dataSnapshot.getChildren()) {
                    String idEventoRecuperado = eventoSnapshot.getKey();

                    for (DataSnapshot userSnapshot : eventoSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        PrePostulacion prePostulacion = userSnapshot.getValue(PrePostulacion.class);

                        if (!prePostulacion.getAceptado() && prePostulacion.getTokenFcmPostulante() != null) {
                            String tokenFcmPostulante = prePostulacion.getTokenFcmPostulante();

                            DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Pre-Postulaciones");
                            postulacionesRef.child(idEventoRecuperado).child(userId).child("aceptado").setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                postularCandidato2(idEventoRecuperado, userId, tokenFcmPostulante);
                                            } else {
                                                // Manejar el error en la actualización
                                            }
                                        }
                                    });

                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });
    }


    private void postularCandidato2(String idEventoRecuperado, String userId, String tokenFcmPostulante) {
        DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(idEventoRecuperado);

        eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);

                    int cupoMaximo = Integer.parseInt(evento.getCupoMaximo());
                    if (cupoMaximo > 0) {
                        int nuevoCupoMaximo = cupoMaximo - 1;
                        evento.setCupoMaximo(String.valueOf(nuevoCupoMaximo));

                        DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");
                        postulacionesRef.child(userId).child(idEventoRecuperado).setValue(true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            eventosRef.setValue(evento)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Realizar la postulación exitosa
                                                                Toast.makeText(getApplicationContext(), "Te has postulado al evento", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                // Error en la modificación del evento
                                                                Toast.makeText(getApplicationContext(), "Error al modificar el evento", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // Error al agregar la información de postulación
                                            Toast.makeText(getApplicationContext(), "Error al postularte al evento", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        // No hay cupo disponible
                        Toast.makeText(getApplicationContext(), "Se alcanzó el cupo máximo", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });
    }


}

