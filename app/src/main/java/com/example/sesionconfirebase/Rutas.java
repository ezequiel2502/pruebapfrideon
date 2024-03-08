package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.Utils.GetNotifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_test.PlanificarRuta;
import com.example.gps_test.Ruta;
import com.example.sesionconfirebase.ActivityRutasRecyclerView.MyListAdapterActivity;
import com.example.sesionconfirebase.ActivityRutasRecyclerView.MyListDataActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;




public class Rutas extends AppCompatActivity {

    private static FirebaseDatabase database;
    private static FirebaseStorage firebaseStorage;
    private static String userId;
    private static MyListAdapterActivity adapter;

    NotificationBadge notificactionBadge;
    private Toolbar toolbar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas);

        database= FirebaseDatabase.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lista_Rutas);
        recyclerView.setHasFixedSize(true);

        ArrayList<MyListDataActivity> data = new ArrayList<>();
        //MyListAdapter adapter=new MyListAdapter(data.toArray(new MyListData[]{}));
        adapter = new MyListAdapterActivity(data);
        adapter.addContext(Rutas.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Rutas.this));

        notificactionBadge=findViewById(R.id.badge);
        toolbar=findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        GetNotifications(currentUser, notificactionBadge);
        database.getReference().child("Route").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    //Object a = dataSnapshot.getChildren().iterator().next();
                    Ruta evento = dataSnapshot.getValue(Ruta.class);
                    String rutaID = dataSnapshot.getKey();
                    if (evento.author.toString().equals(userId))
                    {
                        MyListDataActivity route = new MyListDataActivity(evento.routeName, rutaID, evento.routePoints, evento.type, evento.lenght,
                                evento.curvesAmount, evento.startLocation, evento.finishLocation, evento.imgLocation);
                        data.add(route);
                        //adapter.getCurrentList()[adapter.getCurrentList().length] = route;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        List<UploadTask> activeTasks = firebaseStorage.getReference().child("Usuarios").child(userId).child("routes").getActiveUploadTasks();
        for (UploadTask task: activeTasks)
        {
            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //taskSnapshot.getMetadata().getName();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run(){
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }, 4000);
                }
            });
        }

        FloatingActionButton newRoute = findViewById(com.example.sesionconfirebase.R.id.Agregar_Ruta);
        newRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PlanificarRuta.class);
                intent.putExtra("Close_On_Enter", "False");
                intent.putExtra("User_ID", currentUser.getUid());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        //Barra de navegacion
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.btn_lista_rutas);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_planificar) {
                startActivity(new Intent(getApplicationContext(), ListaEventos.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.btn_perfil) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.btn_inicio) {
                startActivity(new Intent(getApplicationContext(), ListaEventosPublicosVigentes.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            else if (itemId == R.id.btn_lista_postulados) {
                startActivity(new Intent(getApplicationContext(), ListaEventoPostulados.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

    }

    public static void Delete_Route(String databaseID, int position)
    {
        firebaseStorage.getReference().child("Usuarios").child(userId).child("routes").child(databaseID).delete();
        database.getReference().child("Route").child(databaseID).removeValue();
        adapter.getCurrentList().remove(position);
        adapter.notifyItemRemoved(position);
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
