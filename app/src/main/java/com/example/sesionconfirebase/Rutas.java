package com.example.sesionconfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_test.PlanificarRuta;
import com.example.gps_test.Ruta;
import com.example.sesionconfirebase.ActivityRutasRecyclerView.MyListAdapterActivity;
import com.example.sesionconfirebase.ActivityRutasRecyclerView.MyListDataActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;




public class Rutas extends AppCompatActivity {

    private static FirebaseDatabase database;
    private static FirebaseStorage firebaseStorage;
    private static String userId;
    private static MyListAdapterActivity adapter;


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
                    }, 2000);
                }
            });
        }

        Button newRoute = findViewById(com.example.sesionconfirebase.R.id.Agregar_Ruta);
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

    }

    public static void Delete_Route(String databaseID, int position)
    {
        firebaseStorage.getReference().child("Usuarios").child(userId).child("routes").child(databaseID).delete();
        database.getReference().child("Route").child(databaseID).removeValue();
        adapter.notifyItemRemoved(position);
    }

}
