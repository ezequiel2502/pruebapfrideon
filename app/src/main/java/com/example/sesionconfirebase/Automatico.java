package com.example.sesionconfirebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.LinkedList;

public class Automatico {
    private FirebaseUser user;
    NotificationBadge notificactionBadge;
    public Automatico(FirebaseUser user) {
        this.user = user;
    }

    public void setNotificactionBadge(NotificationBadge notificactionBadge) {
        this.notificactionBadge = notificactionBadge;
    }

    public void contarNotificaciones() {
        // Tu código actual para contar notificaciones aquí
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        LinkedList<ModelNotificacion> recycleList = new LinkedList<ModelNotificacion>();
        firebaseDatabase.getReference().child("Notificaciones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelNotificacion notificacion = dataSnapshot.getValue(ModelNotificacion.class);
                    recycleList.add(notificacion);
                }
                NotificationCounter not = new NotificationCounter();
                notificactionBadge.setNumber(not.obtenerCantidadNotificaciones(user.getUid(), recycleList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si es necesario
            }
        });
    }
}
