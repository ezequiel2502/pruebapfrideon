package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.Utils.GetNotifications;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ListaEventoPostulados extends AppCompatActivity {

    RecyclerView recyclerViewEventosPostulados;
    ArrayList<ModelEvento> recycleList;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentUser;
    Spinner spnFiltro;
    ArrayList<String> filtroList = new ArrayList<String>();
    EventoPostuladoAdapter recyclerAdapter;

    NotificationBadge notificactionBadge;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_evento_postulados);

        //Tomo los controles de la vista
        recyclerViewEventosPostulados = findViewById(R.id.recyclerViewEventosPostulados);
        recycleList = new ArrayList<>();

        //********************spnFiltro
        /*spnFiltro = findViewById(R.id.spnFiltro);

        //Lleno la lista de filtros
        filtroList.add("Ninguno");
        filtroList.add("Recientes");*/

        // Crear un ArrayAdapter utilizando la lista de filtros y un diseño simple para el spinner
        ArrayAdapter<String> filtroAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filtroList);

        // Especificar el diseño para el menú desplegable
        filtroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Establecer el adaptador en el Spinner
        /*spnFiltro.setAdapter(filtroAdapter);*/
        //*********************spnFiltro

        notificactionBadge=findViewById(R.id.badge);
        toolbar=findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        // Crear una instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();

        ActivityResultLauncher<Intent> callLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            ListaEventoCompletados.actualizarEventoCompletado();
                            Intent data = result.getData();
                            if (data.getStringExtra("Result").equals("Calificar"))
                            {
                                String eventoId = data.getStringExtra("EventID");
                                DatabaseReference completadosRef = firebaseDatabase.getReference().child("Eventos").child("Completados").child(eventoId);
                                completadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            ModelEvento evento = snapshot.getValue(ModelEvento.class);
                                            Intent intent = new Intent(ListaEventoPostulados.this, CalificarActivity.class);
                                            intent.putExtra("calificacion_gral", String.valueOf(evento.getCalificacionGeneral()));
                                            intent.putExtra("EventoId", evento.getIdEvento());
                                            intent.putExtra("OrganizadorId", evento.getUserId());
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(ListaEventoPostulados.this,"Por favor espere", Toast.LENGTH_LONG).show();
                                            new Timer().schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    completadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (snapshot.exists()) {
                                                                ModelEvento evento = snapshot.getValue(ModelEvento.class);
                                                                Intent intent = new Intent(ListaEventoPostulados.this, CalificarActivity.class);
                                                                intent.putExtra("calificacion_gral", String.valueOf(evento.getCalificacionGeneral()));
                                                                intent.putExtra("EventoId", evento.getIdEvento());
                                                                intent.putExtra("OrganizadorId", evento.getUserId());
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(ListaEventoPostulados.this,"Lo sentimos, los datos aún se están actualizando, intente en unos momentos", Toast.LENGTH_LONG).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
//                                                    runOnUiThread(new Runnable(){
//                                                        @Override
//                                                        public void run(){
//                                                            adapter.notifyDataSetChanged();
//                                                        }
//                                                    });
                                                }
                                            }, 1000);

//                                            DatabaseReference completadosRef = firebaseDatabase.getReference().child("Eventos").child("Eventos Publicos").child(eventoId);
//                                            completadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    ModelEvento evento = snapshot.getValue(ModelEvento.class);
//                                                    Intent intent = new Intent(ListaEventoPostulados.this, CalificarActivity.class);
//                                                    intent.putExtra("calificacion_gral", String.valueOf(evento.getCalificacionGeneral()));
//                                                    intent.putExtra("EventoId", evento.getIdEvento());
//                                                    intent.putExtra("OrganizadorId", evento.getUserId());
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else
                            {
                                ListaEventoCompletados.actualizarEventoCompletado();
                                Intent intent = new Intent(ListaEventoPostulados.this, HomeActivity.class);
                                startActivity(intent);
                                //finish();
                            }
                        }
                    }
                });
        // Crear una instancia del adaptador
        recyclerAdapter = new EventoPostuladoAdapter(recycleList, ListaEventoPostulados.this, callLauncher);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewEventosPostulados.setLayoutManager(linearLayoutManager);
        recyclerViewEventosPostulados.addItemDecoration(new DividerItemDecoration(recyclerViewEventosPostulados.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewEventosPostulados.setNestedScrollingEnabled(false);
        recyclerViewEventosPostulados.setAdapter(recyclerAdapter);

        // Obtener el ID del usuario actualmente logueado
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();


        // Acceder al nodo de "postulaciones" del usuario
        DatabaseReference postulacionesRef = firebaseDatabase.getReference().child("Postulaciones").child(userId);

        GetNotifications(currentUser, notificactionBadge);

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
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                DatabaseReference eventoCompletadoRef = firebaseDatabase.getReference().child("Events_Data").child(currentUser.getUid()).child(evento.getIdEvento());
                                eventoCompletadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            ModelEvento evento = eventoSnapshot.getValue(ModelEvento.class);
                                            // Agregar el evento a la lista
                                            recycleList.add(evento);
                                            recyclerAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                                }
//                                if (eventoSnapshot.exists()) {
//                                    DatabaseReference eventoCompletadoRef = firebaseDatabase.getReference().child("Eventos").child("Completados").child(idEventoPostulado);
//                                    eventoCompletadoRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            if (!snapshot.exists()) {
//                                                ModelEvento evento = eventoSnapshot.getValue(ModelEvento.class);
//                                                // Agregar el evento a la lista
//                                                recycleList.add(evento);
//                                                recyclerAdapter.notifyDataSetChanged();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
//
//                                }
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
            }else if (itemId == R.id.btn_lista_rutas) {
                startActivity(new Intent(getApplicationContext(), Rutas.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            }
            return false;
        });


    }//Fin onCReate()

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

}//fin App


