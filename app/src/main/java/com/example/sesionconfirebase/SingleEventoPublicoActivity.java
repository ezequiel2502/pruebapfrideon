package com.example.sesionconfirebase;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SingleEventoPublicoActivity extends AppCompatActivity {
    TextView tv_SingleEvento,tv_SingleRuta,tv_SingleDescripcion,tv_SingleFechaEncuentro,
            tv_SingleHoraEncuentro,tv_SingleCupoMinimo,tv_SingleCupoMaximo,
            tv_SingleCategoria,tv_SingleUserName,tv_SingleUserId,
            tv_SinglePublicoPrivado,tv_SingleActivadoDescativado;
    ImageView singleImage;

    RatingBar rb_SingleRatingEvento;

    Button btn_postularse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_evento_publico_vigente);


        tv_SingleEvento=findViewById(R.id.tv_SingleEvento);
        tv_SingleRuta=findViewById(R.id.tv_SingleRuta);
        tv_SingleDescripcion=findViewById(R.id.tv_SingleDescripcion);
        tv_SingleFechaEncuentro=findViewById(R.id.tv_SingleFechaEncuentro);
        tv_SingleHoraEncuentro=findViewById(R.id.tv_SingleHoraEncuentro);
        tv_SingleCupoMinimo=findViewById(R.id.tv_SingleCupoMinimo);
        tv_SingleCupoMaximo=findViewById(R.id.tv_SingleCupoMaximo);
        tv_SingleCategoria=findViewById(R.id.tv_SingleCategoria);
        tv_SingleUserName=findViewById(R.id.tv_SingleUserName);
        tv_SingleUserId=findViewById(R.id.tv_SingleUserId);
        tv_SinglePublicoPrivado=findViewById(R.id.tv_SinglePublicoPrivado);
        tv_SingleActivadoDescativado=findViewById(R.id.tv_SingleActivadoDescativado);
        rb_SingleRatingEvento=findViewById(R.id.rb_SingleRatingEvento);
        btn_postularse=findViewById(R.id.btn_postularse);




        //Obtengo los datos de los intents

        singleImage=findViewById(R.id.singleImage);

        String imageUrl = getIntent().getStringExtra("singleImage"); // Obtén la URL de la imagen del Intent
        Glide.with(this)
                .load(imageUrl)
                .into(singleImage); // Carga la imagen en el ImageView del SingleActivity


        String singleEvento = getIntent().getStringExtra("singleEvento");
        String singleRuta = getIntent().getStringExtra("singleRuta");
        String singleDescripcion = getIntent().getStringExtra("singleDescripcion");
        String singleFechaEncuentro = getIntent().getStringExtra("singleFechaEncuentro");
        String singleHoraEncuentro = getIntent().getStringExtra("singleHoraEncuentro");
        String singleCupoMinimo = getIntent().getStringExtra("singleCupoMinimo");
        String singleCupoMaximo = getIntent().getStringExtra("singleCupoMaximo");
        String singleCategoria = getIntent().getStringExtra("singleCategoria");
        String singleUserName=getIntent().getStringExtra("singleUserName");
        String singleUserId=getIntent().getStringExtra("singleUserId");
        String singleActivarDesactivar=getIntent().getStringExtra("singleActivarDesactivar");
        String singlePublicoPrivado=getIntent().getStringExtra("singlePublicoPrivado");
        Integer singleRating=getIntent().getIntExtra("singleRating",0);
        String singleIdEvento=getIntent().getStringExtra("EventoId");

        // Establece los datos en los TextViews
        tv_SingleEvento.setText(singleEvento);
        tv_SingleRuta.setText(singleRuta);
        tv_SingleDescripcion.setText(singleDescripcion);
        tv_SingleFechaEncuentro.setText(singleFechaEncuentro);
        tv_SingleHoraEncuentro.setText(singleHoraEncuentro);
        tv_SingleCupoMinimo.setText(singleCupoMinimo);
        tv_SingleCupoMaximo.setText(singleCupoMaximo);
        tv_SingleCategoria.setText(singleCategoria);
        tv_SingleUserName.setText(singleUserName);
        tv_SingleUserId.setText(singleUserId);
        rb_SingleRatingEvento.setRating(singleRating);
        tv_SinglePublicoPrivado.setText(singlePublicoPrivado);
        tv_SingleActivadoDescativado.setText(singleActivarDesactivar);


        //Referencia al nodo postulaciones que uso dentro del boton Postularse
        DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");

        //Boton Postularse
        btn_postularse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Buscar el evento
                DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(singleIdEvento);

                eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);

                            // Verificar si hay cupo disponible
                            int cupoMaximo = Integer.parseInt(evento.getCupoMaximo());
                            if (cupoMaximo > 0) {
                                // Modificar el valor del cupoMaximo
                                int nuevoCupoMaximo = cupoMaximo - 1;
                                evento.setCupoMaximo(String.valueOf(nuevoCupoMaximo));

                                // Obtener el ID del usuario actual
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                // Agregar la información de postulación al nodo "postulaciones"
                                DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");
                                postulacionesRef.child(userId).child(singleIdEvento).setValue(true)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Actualizar el evento modificado en la base de datos
                                                    eventosRef.setValue(evento)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        // Modificación exitosa
                                                                        Toast.makeText(getApplicationContext(), "Te has postulado al evento", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        // Error en la modificación del evento
                                                                        Toast.makeText(getApplicationContext(), "Error al modificar el evento", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    // Error al agregar la información de postulación
                                                    Toast.makeText(getApplicationContext(), "Error al postularte al evento", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // No hay cupo disponible
                                Toast.makeText(getApplicationContext(), "Se alcanzó el cupo máximo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar error de cancelación
                    }
                });
            }
        });




    }//fin onCreate()

}//Fin Appp
