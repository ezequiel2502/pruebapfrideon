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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calificar_evento);

        //tomo los controles de la vista
        rb_ratingGeneral=findViewById(R.id.rb_ratingGeneral);
        rb_userRating=findViewById(R.id.rb_userRating);
        btn_agregarCalificacion=findViewById(R.id.btn_agregarCalificacion);


        // Recibo la calificacion gral
        float calificacion_gral = getIntent().getFloatExtra("calificacion_gral", 0.0f);
        String idEvento=getIntent().getStringExtra("EventoId");


        // Establecer el rating Gereneral
        rb_ratingGeneral.setRating(calificacion_gral);


        //Accedo al no de eventos publicos para recuperar el evento
        // Acceder al nodo de "Eventos Publicos" usando el idEvento
        DatabaseReference eventosPublicosRef = firebaseDatabase.getReference().child("Eventos").child("Eventos Publicos").child(idEvento);

        eventosPublicosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    evento = dataSnapshot.getValue(ModelEvento.class);




                } else {
                    // El evento con el id proporcionado no existe
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });


        btn_agregarCalificacion.setOnClickListener(new View.OnClickListener() {

            //Obtengo la calificacion ingresada por el usuario
            float calificacionUsuario = rb_userRating.getRating();

            @Override
            public void onClick(View view) {

                if (evento != null) {

                    // Crea una nueva instancia de ModelCalificacion
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    ModelCalificacion nuevaCalificacion = new ModelCalificacion(userId, calificacionUsuario);

                    // Agrega la nueva calificación al objeto evento
                    evento.agregarCalificacion(nuevaCalificacion);

                    // Recalcula la calificación promedio y la setea en calificacionGeneral
                    evento.calcularYSetearCalificacionPromedio();

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
