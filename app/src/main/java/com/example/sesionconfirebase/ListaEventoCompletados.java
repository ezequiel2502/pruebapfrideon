package com.example.sesionconfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_test.PlanificarRuta;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaEventoCompletados extends AppCompatActivity {

    RecyclerView recyclerViewEventosCompletados;
    ArrayList<ModelEvento> recycleList;

    FirebaseDatabase firebaseDatabase;

    Spinner spnFiltro;

    ArrayList<String> filtroList = new ArrayList<String>();

    EventoCompletadoAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos_completados);

        //Tomo los controles de la vista
        recyclerViewEventosCompletados = findViewById(R.id.recyclerViewEventosCompletados);
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
        recyclerAdapter = new EventoCompletadoAdapter(recycleList, ListaEventoCompletados.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewEventosCompletados.setLayoutManager(linearLayoutManager);
        recyclerViewEventosCompletados.addItemDecoration(new DividerItemDecoration(recyclerViewEventosCompletados.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewEventosCompletados.setNestedScrollingEnabled(false);
        recyclerViewEventosCompletados.setAdapter(recyclerAdapter);


        //********************Observo cambios en el nodo events_data

        //// Obtener el ID del usuario actualmente logueado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Acceder al nodo de "Events_Data" del usuario
        DatabaseReference eventsDataRef = firebaseDatabase.getReference().child("Events_Data").child(userId);

        eventsDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventoSnapshot : dataSnapshot.getChildren()) {
                    String eventoId = eventoSnapshot.getKey();
                    DatabaseReference eventoPublicoRef = firebaseDatabase.getReference().child("Eventos").child("Eventos Publicos").child(eventoId);

                    eventoPublicoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);

                                DatabaseReference completadosRef = firebaseDatabase.getReference().child("Eventos").child("Completados").child(eventoId);
                                completadosRef.setValue(evento);

                                // Remover el evento de "Eventos Publicos"
                                eventoPublicoRef.removeValue();
                            }

                            // Acceder a los eventos completados usando este eventoId
                            DatabaseReference eventosCompletadosRef = firebaseDatabase.getReference().child("Eventos").child("Completados").child(eventoId);

                            eventosCompletadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot eventoSnapshot) {
                                    if (eventoSnapshot.exists()) {
                                        ModelEvento evento = eventoSnapshot.getValue(ModelEvento.class);
                                        recycleList.add(evento);
                                        recyclerAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Manejar error de cancelación
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Manejar error de cancelación
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });

        //********************Observo cambios en el nodo events_data


        //Barra de navegacion

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.btn_perfil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_perfil) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            } else if (itemId == R.id.btn_inicio) {
                startActivity(new Intent(getApplicationContext(), ListaEventosPublicosVigentes.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            } else if (itemId == R.id.btn_planificar) {
                startActivity(new Intent(getApplicationContext(), ListaEventos.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            }
            else if (itemId == R.id.btn_lista_postulados) {
                startActivity(new Intent(getApplicationContext(), ListaEventoPostulados.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            }
            else if (itemId == R.id.btn_lista_rutas) {
                startActivity(new Intent(getApplicationContext(), Rutas.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            }
            return false;
        });


    }//fin onCreate()
}//fin App
