package com.example.sesionconfirebase;

import androidx.annotation.NonNull;

import com.example.gps_test.ui.ActivityBuscarEventosRecycler.ModelEvento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Lista_Usuarios_Por_Evento {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference refPostulados = database.getReference().child("Postulaciones");

    public List<String> getUsersPerEvent() {
        List<String> list = new ArrayList<>();
        String EventKey = null; //Acá iria el valor identificador del evento del que buscamos los usuarios

        refPostulados.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    if (dataSnapshot.child(EventKey).exists())
                    {
                        list.add(dataSnapshot.getKey());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return list;
    }
}
