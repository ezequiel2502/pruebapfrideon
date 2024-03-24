package com.example.sesionconfirebase;

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
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListadoNotificacionesActivity extends AppCompatActivity {
    RecyclerView recyclerViewNotificaciones;
    ArrayList<ModelNotificacion> recycleList;
    FirebaseDatabase firebaseDatabase;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_notificaciones);
        recyclerViewNotificaciones = findViewById(R.id.recyclerViewNotificaciones);
        recycleList = new ArrayList<>();
        //Creo la instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
// Obtener referencias a los botones
        //Button btnVolver;
        //btnVolver = findViewById(R.id.btnVolver);
        ImageView btnActualizar;
        btnActualizar = findViewById(R.id.btnActualizar);

        toolbar=findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        // Configurar el OnClickListener para el botón "Volver"
/*        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListadoNotificacionesActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });*/

        // Configurar el OnClickListener para el botón "Actualizar"
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recycleList.clear();
                cargarDatos();
                // Notificar al RecyclerView que los datos han cambiado
                recyclerViewNotificaciones.getAdapter().notifyDataSetChanged();
            }
        });
        cargarDatos();

    }
    private void cargarDatos() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Creo una instancia del adapter
        NotificacionesAdapter recyclerAdapter = new NotificacionesAdapter(recycleList, ListadoNotificacionesActivity.this);
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
                    if (currentUser != null && Notificacion !=null) {
                        String userID = currentUser.getUid();
                        if(Notificacion.getIdOrganizador() != null) {
                            if(Notificacion.getIdOrganizador().equals(userID) && (Notificacion.getTipoNotificacion().equals("creador_evento") || Notificacion.getTipoNotificacion().equals("cupo-maximo") || Notificacion.getTipoNotificacion().equals("cancela_postulante_evento")))
                            {
                                recycleList.add(Notificacion);
                            }
                        } else if ( Notificacion.getPostulanteId()!=null) {
                            if(Notificacion.getPostulanteId().equals(userID) && (Notificacion.getTipoNotificacion().equals("denegacion_postulante_evento") || Notificacion.getTipoNotificacion().equals("postulante_evento") || Notificacion.getTipoNotificacion().equals("cancelacion_evento")))
                            {
                                recycleList.add(Notificacion);
                            }
                        }
                    }
                }
                // Verificar si la lista está vacía y redirigir si es necesario
                if (recycleList.isEmpty()) {
                    Intent intent = new Intent(ListadoNotificacionesActivity.this, HomeActivity.class);
                    startActivity(intent);
                    Toast.makeText(ListadoNotificacionesActivity.this, "No hay más Notificaciones", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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