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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaEventoPostulados extends AppCompatActivity {

    RecyclerView recyclerViewEventosPostulados;
    ArrayList<ModelEvento> recycleList;
    FirebaseDatabase firebaseDatabase;
    Spinner spnFiltro;
    ArrayList<String> filtroList = new ArrayList<String>();
    EventoPostuladoAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_evento_postulados);

        //Tomo los controles de la vista
        recyclerViewEventosPostulados = findViewById(R.id.recyclerViewEventosPostulados);
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

        // Crear una instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Crear una instancia del adaptador
        recyclerAdapter = new EventoPostuladoAdapter(recycleList, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewEventosPostulados.setLayoutManager(linearLayoutManager);
        recyclerViewEventosPostulados.addItemDecoration(new DividerItemDecoration(recyclerViewEventosPostulados.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewEventosPostulados.setNestedScrollingEnabled(false);
        recyclerViewEventosPostulados.setAdapter(recyclerAdapter);

        // Obtener el ID del usuario actualmente logueado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // Acceder al nodo de "postulaciones" del usuario
        DatabaseReference postulacionesRef = firebaseDatabase.getReference().child("Postulaciones").child(userId);

        postulacionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postulacionSnapshot : dataSnapshot.getChildren()) {

                    Boolean postulacionAceptada = postulacionSnapshot.getValue(Boolean.class);

                    // Verificar si la postulación está aceptada (true)
                    if (postulacionAceptada != null && postulacionAceptada) {

                        //tomo el id de ese evento guardado en postulaciones exitosas
                        String idEventoPostulado = postulacionSnapshot.getKey();

                        // Buscar y listar los detalles de los eventos públicos correspondientes con idEventoPostulado
                        DatabaseReference eventosPublicosRef = firebaseDatabase.getReference().child("Eventos").child("Eventos Publicos");
                        eventosPublicosRef.child(idEventoPostulado).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot eventoSnapshot) {
                                if (eventoSnapshot.exists()) {
                                    ModelEvento evento = eventoSnapshot.getValue(ModelEvento.class);
                                    // Agregar el evento a la lista
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });


        //Barra de navegacion
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.btn_lista_postulados);
        bottomNavigationView.setOnItemSelectedListener(item ->

        {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_lista_postulados) {
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
            } else if (itemId == R.id.btn_inicio) {
                startActivity(new Intent(getApplicationContext(), ListaEventosPublicosVigentes.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });


    }//Fin onCReate()


}//fin App


