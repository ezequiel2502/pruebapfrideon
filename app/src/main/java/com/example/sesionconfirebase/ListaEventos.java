package com.example.sesionconfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaEventos extends AppCompatActivity {

    Button btnAgregarEvento;
    RecyclerView recyclerViewEventos;
    ArrayList<ModelEvento> recycleList;

    FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos);

        //Obtengo el ID del usuario logueado en Firebase Authentication:
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();


        //Tomo los controles de la vista
        btnAgregarEvento=findViewById(R.id.btnAgregarEvento);
        recyclerViewEventos=findViewById(R.id.recyclerViewEventos);
        recycleList=new ArrayList<>();

        //>>>>>>>>>>spnFiltro
        spnFiltro=findViewById(R.id.spnFiltro);

        //Lleno la lista de filtros
        filtroList.add("Ninguno");
        filtroList.add("Recientes");

        // Crear un ArrayAdapter utilizando la lista de filtros y un diseño simple para el spinner
        ArrayAdapter<String> filtroAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filtroList);

        // Especificar el diseño para el menú desplegable
        filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Establecer el adaptador en el Spinner
        spnFiltro.setAdapter(filtroAdapter);
        //spnFiltro<<<<<<<<<<

        //Creo la instancia de la base de datos
        firebaseDatabase= FirebaseDatabase.getInstance();

        //Creo una instancia del adapter
        EventoAdapter recyclerAdapter=new EventoAdapter(recycleList,getApplicationContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerViewEventos.setLayoutManager(linearLayoutManager);
        recyclerViewEventos.addItemDecoration(new DividerItemDecoration(recyclerViewEventos.getContext(),DividerItemDecoration.VERTICAL));
        recyclerViewEventos.setNestedScrollingEnabled(false);
        recyclerViewEventos.setAdapter(recyclerAdapter);

        //Si hay un cambio en la base de datos lo mete en la lista de eventos
        firebaseDatabase.getReference().child("Usuarios").child(userId).child("Eventos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ModelEvento evento=dataSnapshot.getValue(ModelEvento.class);
                    recycleList.add(evento);
                }

                recyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Filtros...por ahora solo fecha de encuentro
        spnFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String opcionSeleccionada = filtroList.get(i);

                if (opcionSeleccionada.equals("Recientes")) {
                    recyclerAdapter.ordenarListaPorFecha();
                    //Toast.makeText(ListaEventos.this, "Apretaste Recientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        //Boton crearEvento
        btnAgregarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ListaEventos.this, CrearEvento.class);
                startActivity(intent);
            }
        });



        //Barra de navegacion
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.btn_planificar);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_planificar) {
                return true;
            } else if (itemId == R.id.btn_perfil) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.btn_inicio) {
                startActivity(new Intent(getApplicationContext(), Inicio.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }//fin onCreate()
}//fin App