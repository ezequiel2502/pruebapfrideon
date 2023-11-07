package com.example.sesionconfirebase;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationActionReceiver extends BroadcastReceiver {

    private Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {

        // Guardar el contexto cuando se recibe la notificación
        mContext = context.getApplicationContext();

        String action = intent.getStringExtra("ACTION");

        if ("Botón 1".equals(action)) {
            // Aquí puedes enviar un broadcast específico para capturar la acción en la SingleEventoPublicoActivity
//            Intent broadcastIntent = new Intent("com.example.sesionconfirebase.ACTION_POSTULAR");
//            context.sendBroadcast(broadcastIntent);
            
            // Recuperar los datos pasados al servicio
            String idEvento = intent.getStringExtra("idEvento");
            String postulanteId = intent.getStringExtra("postulanteId");
            String nombreEvento = intent.getStringExtra("nombreEvento");
            String tokenCreador = intent.getStringExtra("tokenCreador");
            String tokenPostulante = intent.getStringExtra("tokenPostulante");

            // Obtén una referencia a tus SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("SPNotificationActionReceiver", Context.MODE_PRIVATE);

            // Obtiene un editor para modificar los valores
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Guarda los valores
            editor.putString("idEvento", idEvento);
            editor.putString("postulanteId", postulanteId);
            editor.putString("nombreEvento", nombreEvento);
            editor.putString("tokenCreador", tokenCreador);
            editor.putString("tokenPostulante", tokenPostulante);

            // Aplica los cambios
            editor.apply();

            // Ejecuta tu método para aceptar

            //buscarPrimerNoAceptado(context);
            buscarNoAceptadoPorEventoYUsuario(context,idEvento,nombreEvento,postulanteId);
            //notificarPostulanteEvento(context,nombreEvento,tokenPostulante);

        } else if ("Botón 2".equals(action)) {

            // Aquí envías un broadcast específico para capturar la acción del Botón 2 en la SingleEventoPublicoActivity
//            Intent broadcastIntent = new Intent("com.example.sesionconfirebase.ACTION_DENEGAR_POSTULACION");
//            context.sendBroadcast(broadcastIntent);

            String nombreEvento = intent.getStringExtra("nombreEvento");
            String IdPostulante = intent.getStringExtra("IdPostulante");
            String IdEvento = intent.getStringExtra("IdEvento");
            notificarDenegacionPostulanteEvento(IdEvento,nombreEvento,IdPostulante);
        }
    }




    private void buscarNoAceptadoPorEventoYUsuario(Context context,String idEvento,String nombreEvento,String userId) {
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

                                        //si salio bien notifica y postula.
                                        notificarPostulanteEvento(idEvento,nombreEvento,tokenFcmPostulante);

                                        postularCandidato2(context,idEvento, userId, tokenFcmPostulante);
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

    private void postularCandidato2(Context context, String idEventoRecuperado, String userId, String tokenFcmPostulante) {
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

                                            // Agregamos al participante a la lista
                                            evento.agregarParticipante(userId);

                                            // Guardamos los cambios en el evento
                                            eventosRef.setValue(evento)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                if (nuevoCupoMaximo == 0) {
                                                                    // Notificar al creador del evento
                                                                    notificarCupoMaximoAlcanzado(evento.getNombreEvento(),evento.getIdEvento());
                                                                }

                                                                // Realizar la postulación exitosa
                                                                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                // Error en la modificación del evento
                                                                Toast.makeText(context, "Error al modificar el evento", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // Error al agregar la información de postulación
                                            Toast.makeText(context, "Error al postularte al evento", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        // No hay cupo disponible
                        Toast.makeText(context, "Se alcanzó el cupo máximo", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });
    }
    private void notificarCupoMaximoAlcanzado(String nombreEvento,String idEvento){

//        SharedPreferences sharedPreferences = mContext.getSharedPreferences("SPNotificationActionReceiver", Context.MODE_PRIVATE);
//
//        String idEvento = sharedPreferences.getString("idEvento", "");
//        String nombreEvento = sharedPreferences.getString("nombreEvento", "");
//        String tokenCreador = sharedPreferences.getString("tokenCreador", "");
        NotificationCounter notificacion = new NotificationCounter();
// Crea una referencia al evento que quieres recuperar
        DatabaseReference eventoRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(idEvento);

// Agrega un listener para escuchar los cambios en el evento
        eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Se ejecuta cuando los datos del evento han cambiado
                if (dataSnapshot.exists()) {
                    // El evento existe en la base de datos
                    // Ahora puedes obtener el idOrganizador
                    String idOrganizador = dataSnapshot.child("userId").getValue(String.class);
                    notificacion.registrarNotificacionCupoMaximoAlcanzado("Cupo Máximo Alcanzado en:",nombreEvento,"cupo-maximo",idEvento,idOrganizador,nombreEvento);
                } else {
                    // El evento no existe en la base de datos
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Se ejecuta si hay un error en la operación
                System.out.println("Error al recuperar el evento: " + databaseError.getMessage());
            }
        });
    }
    private void buscarPrimerNoAceptado(Context context) {
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
                                                postularCandidato2(context,idEventoRecuperado,userId, tokenFcmPostulante);
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
    private void notificarPostulanteEvento( String IdEvento,String nombreEvento,String postulanteId) {
        NotificationCounter notificacion = new NotificationCounter();
        notificacion.registrarNotificacionPostulanteEvento("Aceptaron tu postulacion a :",nombreEvento,"postulante_evento",IdEvento,postulanteId,nombreEvento);
    }

    private void notificarDenegacionPostulanteEvento( String IdEvento,String nombreEvento, String postulanteId) {
        NotificationCounter notificacion = new NotificationCounter();
        notificacion.registrarNotificacionDenegacionPostulanteEvento("Denegaron tu postulacion a : ",nombreEvento,"denegacion_postulante_evento",IdEvento,postulanteId,nombreEvento);
    }


    
}//fin NotificactionActionReceiver
