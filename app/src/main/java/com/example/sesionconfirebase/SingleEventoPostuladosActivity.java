package com.example.sesionconfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SingleEventoPostuladosActivity extends AppCompatActivity {

    TextView tv_SingleEvento, tv_SingleRuta, tv_SingleDescripcion, tv_SingleFechaEncuentro,
            tv_SingleHoraEncuentro, tv_SingleCupoMinimo, tv_SingleCupoMaximo,
            tv_SingleCategoria, tv_SingleUserName, tv_SingleUserId,
            tv_SinglePublicoPrivado, tv_SingleActivadoDescativado;
    ImageView singleImage;

    RatingBar rb_SingleRatingEvento;

    Button btn_CancelarPostulacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_evento_postulados);


        tv_SingleEvento = findViewById(R.id.tv_SingleEvento);
        tv_SingleRuta = findViewById(R.id.tv_SingleRuta);
        tv_SingleDescripcion = findViewById(R.id.tv_SingleDescripcion);
        tv_SingleFechaEncuentro = findViewById(R.id.tv_SingleFechaEncuentro);
        tv_SingleHoraEncuentro = findViewById(R.id.tv_SingleHoraEncuentro);
        tv_SingleCupoMinimo = findViewById(R.id.tv_SingleCupoMinimo);
        tv_SingleCupoMaximo = findViewById(R.id.tv_SingleCupoMaximo);
        tv_SingleCategoria = findViewById(R.id.tv_SingleCategoria);
        tv_SingleUserName = findViewById(R.id.tv_SingleUserName);
        tv_SingleUserId = findViewById(R.id.tv_SingleUserId);
        tv_SinglePublicoPrivado = findViewById(R.id.tv_SinglePublicoPrivado);
        tv_SingleActivadoDescativado = findViewById(R.id.tv_SingleActivadoDescativado);
        rb_SingleRatingEvento = findViewById(R.id.rb_SingleRatingEvento);
        btn_CancelarPostulacion = findViewById(R.id.btn_CancelarPostulacion);

        //Obtengo los datos de los intents

        singleImage = findViewById(R.id.singleImage);

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
        String singleUserName = getIntent().getStringExtra("singleUserName");
        String singleUserId = getIntent().getStringExtra("singleUserId");
        String singleActivarDesactivar = getIntent().getStringExtra("singleActivarDesactivar");
        String singlePublicoPrivado = getIntent().getStringExtra("singlePublicoPrivado");
        Integer singleRating = getIntent().getIntExtra("singleRating", 0);
        String singleIdEvento = getIntent().getStringExtra("EventoId");

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


        //Botón Cancelar Postulación
        btn_CancelarPostulacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Buscar el evento
                DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(singleIdEvento);

                eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);

                            // Incrementar el valor del cupoMaximo
                            int cupoMaximo = Integer.parseInt(evento.getCupoMaximo());
                            int nuevoCupoMaximo = cupoMaximo + 1;
                            evento.setCupoMaximo(String.valueOf(nuevoCupoMaximo));

                            // Obtener el ID del usuario actual
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Eliminar la referencia de postulación del evento específico
                            DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");
                            postulacionesRef.child(userId).child(singleIdEvento).removeValue()
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
                                                                    // Cancelación exitosa
                                                                    Toast.makeText(getApplicationContext(), "Has cancelado tu postulación al evento", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    // Error en la modificación del evento
                                                                    Toast.makeText(getApplicationContext(), "Error al modificar el evento", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                // Error al eliminar la referencia de postulación
                                                Toast.makeText(getApplicationContext(), "Error al cancelar la postulación al evento", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar error de cancelación
                    }
                });
            }
        });


    }
}