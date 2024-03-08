package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.Utils.GetNotifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;

public class ListaEventosPublicosVigentes extends AppCompatActivity {

    RecyclerView recyclerViewEventosPublicosVigentes;

    ArrayList<ModelEvento> recycleList;

    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentUser;
    Spinner spnFiltro;

    ArrayList<String> filtroList = new ArrayList<String>();

    NotificationBadge notificactionBadge;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventos_publicos_vigentes);

        //Tomo los controles de la vista
        recyclerViewEventosPublicosVigentes = findViewById(R.id.recyclerViewEventosPublicosVigentes);
        recycleList = new ArrayList<>();

        //********************spnFiltro
        //spnFiltro = findViewById(R.id.spnFiltro);

        //Lleno la lista de filtros
        /*filtroList.add("Ninguno");
        filtroList.add("Recientes");*/

        // Crear un ArrayAdapter utilizando la lista de filtros y un diseño simple para el spinner
        //ArrayAdapter<String> filtroAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filtroList);

        // Especificar el diseño para el menú desplegable
        //filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Establecer el adaptador en el Spinner
        //spnFiltro.setAdapter(filtroAdapter);
        //*********************spnFiltro


        //Creo la instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        notificactionBadge=findViewById(R.id.badge);
        toolbar=findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        GetNotifications(currentUser, notificactionBadge);

        //Creo una instancia del adapter
        EventoPublicoAdapter recyclerAdapter = new EventoPublicoAdapter(recycleList, ListaEventosPublicosVigentes.this);
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
                    if (dataSnapshot.exists())
                    {
                        ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);
                        DatabaseReference eventoCompletadoRef = firebaseDatabase.getReference().child("Eventos").child("Completados").child(evento.getIdEvento());
                        eventoCompletadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()) {
                                    recycleList.add(evento);
                                    recyclerAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }


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
            }else if (itemId == R.id.btn_lista_rutas) {
                startActivity(new Intent(getApplicationContext(), Rutas.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            }
            return false;
        });
    }

    public void redirectToOtherActivity(View view) {
        // Crea un Intent para abrir la otra actividad
        Intent intent = new Intent(this, ListadoNotificacionesActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}