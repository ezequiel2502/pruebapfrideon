package com.example.sesionconfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaEventosPublicosVigentes extends AppCompatActivity {

    RecyclerView recyclerViewEventosPublicosVigentes;

    ArrayList<ModelEvento> recycleList;

    FirebaseDatabase firebaseDatabase;

    Spinner spnFiltro;

    ArrayList<String> filtroList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos_publicos_vigentes);

        //Tomo los controles de la vista
        recyclerViewEventosPublicosVigentes = findViewById(R.id.recyclerViewEventosPublicosVigentes);
        recycleList = new ArrayList<>();

        //********************spnFiltro
        spnFiltro = findViewById(R.id.spnFiltro);

        //Lleno la lista de filtros
        filtroList.add("Ninguno");
        filtroList.add("Recientes");

        // Crear un ArrayAdapter utilizando la lista de filtros y un diseño simple para el spinner
        ArrayAdapter<String> filtroAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filtroList);

        // Especificar el diseño para el menú desplegable
        filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Establecer el adaptador en el Spinner
        spnFiltro.setAdapter(filtroAdapter);
        //*********************spnFiltro


        //Creo la instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Creo una instancia del adapter
        EventoPublicoAdapter recyclerAdapter = new EventoPublicoAdapter(recycleList, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewEventosPublicosVigentes.setLayoutManager(linearLayoutManager);
        recyclerViewEventosPublicosVigentes.addItemDecoration(new DividerItemDecoration(recyclerViewEventosPublicosVigentes.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewEventosPublicosVigentes.setNestedScrollingEnabled(false);
        recyclerViewEventosPublicosVigentes.setAdapter(recyclerAdapter);

        //Reviso cambios en los eventos privados, si hubo alguno lo meto en la lista
        firebaseDatabase.getReference().child("Eventos").child("Eventos Publicos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);
                    recycleList.add(evento);
                }

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Barra de navegacion
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.btn_inicio);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_inicio) {
                return true;
            } else if (itemId == R.id.btn_perfil) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.btn_planificar) {
                startActivity(new Intent(getApplicationContext(), ListaEventos.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.btn_lista_postulados) {
                startActivity(new Intent(getApplicationContext(), ListaEventoPostulados.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }
}