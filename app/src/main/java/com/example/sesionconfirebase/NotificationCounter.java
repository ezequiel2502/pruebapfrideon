package com.example.sesionconfirebase;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class NotificationCounter {

    // Método para registrar una notificación para un usuario

    public boolean registrarNotificacion(String titulo, String detalle,String tipoNotificacion,String idEvento,String postulanteId,String nombreEvento,String tokenCreador,String tokenPostulante)
    {
      FirebaseDatabase database;
      FirebaseStorage firebaseStorage;
        final Boolean[] ban = {false};
        database=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        ModelNotificacion notificacion = new ModelNotificacion();
        notificacion.setDetalle(detalle);
        notificacion.setTipoNotificacion(tipoNotificacion);
        notificacion.setTitulo(titulo);
        notificacion.setIdEvento(idEvento);
        notificacion.setPostulanteId(postulanteId);
        notificacion.setNombreEvento(nombreEvento);
        notificacion.setTokenCreador(tokenCreador);
        notificacion.setTokenPostulante(tokenPostulante);
        DatabaseReference notificacionRef = database.getReference().child("Notificaciones");

        DatabaseReference nuevonotificacionRef = notificacionRef.push();
        String nuevoNotificacionId = nuevonotificacionRef.getKey();
        notificacion.setIdNotificacion(nuevoNotificacionId);
        nuevonotificacionRef.setValue(notificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // La inserción fue exitosa
                ban[0] = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // La inserción falló
                ban[0] = false;
            }
        });
        return ban[0];
    }

    // Método para obtener la cantidad de notificaciones para un usuario
    public int obtenerCantidadNotificaciones(String userId) {
        int cantidadNotificaciones = 14;
        return cantidadNotificaciones;
    }
}
