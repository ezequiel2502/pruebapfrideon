package com.example.sesionconfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CalificarActivity extends AppCompatActivity {

Button btn_agregarCalificacion;

RatingBar rb_ratingGeneral,rb_userRating;

FirebaseDatabase firebaseDatabase;

ModelEvento evento;

ModelUsuario perfilOrganizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calificar_evento);

        //tomo los controles de la vista
        rb_ratingGeneral=findViewById(R.id.rb_ratingGeneral);
        rb_userRating=findViewById(R.id.rb_userRating);
        btn_agregarCalificacion=findViewById(R.id.btn_agregarCalificacion);



        //Creo la instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Recibo los intents desde la singleEventoCompletadoActivity
        float calificacion_gral = getIntent().getFloatExtra("calificacion_gral", 0.0f);
        String idEvento = getIntent().getStringExtra("EventoId");
        String OrganizadorId = getIntent().getStringExtra("OrganizadorId");


        //Establecer el rating Gereneral
        rb_ratingGeneral.setRating(calificacion_gral);




        //Referencia al nodo "Completados"
        DatabaseReference eventosCompletadosRef = firebaseDatabase.getReference().child("Eventos").child("Completados").child(idEvento);
        eventosCompletadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El evento se encuentra en "Completados"
                    evento = dataSnapshot.getValue(ModelEvento.class);
                } else {
                    // El evento no se encuentra en ninguno de los nodos
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });







        btn_agregarCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Obtengo la calificacion ingresada por el usuario
                float calificacionUsuario = rb_userRating.getRating();

                if (evento != null) {

                    // Crea una nueva instancia de ModelCalificacion
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ModelCalificacion nuevaCalificacion = new ModelCalificacion(userId, calificacionUsuario);

                    // Agrega la nueva calificación al objeto evento
                    evento.agregarCalificacion(nuevaCalificacion);

                    // Recalcula la calificación promedio y la setea en calificacionGeneral
                    evento.calcularYSetearCalificacionPromedio();

                    //agrego el objeto a la base de datos
                    firebaseDatabase.getReference().child("Eventos").child("Eventos Publicos").child(idEvento).setValue(evento);


                    //firebaseDatabase.getReference().child("Perfil").child(OrganizadorId);



                    // Muestra un Toast indicando que se agregó correctamente la calificación
                    Toast.makeText(CalificarActivity.this, "Se agregó correctamente tu calificación", Toast.LENGTH_LONG).show();


                    // Vuelvo a la singleActivityEventoCompletado con la nueva calificacion general
                    Intent intent = new Intent(CalificarActivity.this, SingleEventoCompletadoActivity.class);
                    intent.putExtra("singleRating", evento.getCalificacionGeneral());
                    startActivity(intent);

                } else {

                }


            }
        });


    }//fin onCreate()



}//fin App
