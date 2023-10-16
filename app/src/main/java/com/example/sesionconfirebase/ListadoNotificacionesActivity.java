package com.example.sesionconfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListadoNotificacionesActivity extends AppCompatActivity {
    RecyclerView recyclerViewNotificaciones;
    ArrayList<ModelNotificacion> recycleList;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_notificaciones);
        recyclerViewNotificaciones = findViewById(R.id.recyclerViewNotificaciones);
         recycleList = new ArrayList<>();
        //Creo la instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Creo una instancia del adapter
        NotificacionesAdapter recyclerAdapter = new NotificacionesAdapter(recycleList, ListaNotificaciones.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewNotificaciones.setLayoutManager(linearLayoutManager);
        recyclerViewNotificaciones.addItemDecoration(new DividerItemDecoration(recyclerViewNotificaciones.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewNotificaciones.setNestedScrollingEnabled(false);
        recyclerViewNotificaciones.setAdapter(recyclerAdapter);

        //Reviso cambios en los eventos privados, si hubo alguno lo meto en la lista
        firebaseDatabase.getReference().child("Notificaciones").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelNotificacion Notificacion = dataSnapshot.getValue(ModelNotificacion.class);
                    recycleList.add(Notificacion);
                }
                recyclerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}