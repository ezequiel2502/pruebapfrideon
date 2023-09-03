package com.example.sesionconfirebase;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("ACTION");

        if ("Botón 1".equals(action)) {
            // Aquí puedes enviar un broadcast específico para capturar la acción en la SingleEventoPublicoActivity
//            Intent broadcastIntent = new Intent("com.example.sesionconfirebase.ACTION_POSTULAR");
//            context.sendBroadcast(broadcastIntent);
            
            // Recuperar los datos pasados al servicio
            String idEvento = intent.getStringExtra("idEvento");
            String postulanteId = intent.getStringExtra("postulanteId");

            // Ejecuta tu método para aceptar
            //buscarPrimerNoAceptado(context);
            buscarNoAceptadoPorEventoYUsuario(context,idEvento,postulanteId);

        } else if ("Botón 2".equals(action)) {
            // Aquí envías un broadcast específico para capturar la acción del Botón 2 en la SingleEventoPublicoActivity
            Intent broadcastIntent = new Intent("com.example.sesionconfirebase.ACTION_DENEGAR_POSTULACION");
            context.sendBroadcast(broadcastIntent);
        }
    }


    private void buscarNoAceptadoPorEventoYUsuario(Context context,String idEvento, String userId) {
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
                                            eventosRef.setValue(evento)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
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
    
    
    
    
}
