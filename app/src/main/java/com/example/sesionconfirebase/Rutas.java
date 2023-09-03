package com.example.sesionconfirebase;

import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_test.Ruta;
import com.example.sesionconfirebase.R;
import com.example.sesionconfirebase.SeleccionarRutaRecyclerView.MyListAdapter;
import com.example.sesionconfirebase.SeleccionarRutaRecyclerView.MyListData;
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

    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas);

        database= FirebaseDatabase.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lista_Rutas);
        recyclerView.setHasFixedSize(true);

        ArrayList<MyListData> data = new ArrayList<>();
        //MyListAdapter adapter=new MyListAdapter(data.toArray(new MyListData[]{}));
        MyListAdapter adapter=new MyListAdapter(data);
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
                        MyListData route = new MyListData(evento.routeName, rutaID, evento.routePoints, evento.type, evento.lenght,
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

    }
}
