package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.Utils.GetNotifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

public class CalificarActivity extends AppCompatActivity {

Button btn_agregarCalificacion;

RatingBar rb_ratingGeneral,rb_userRating;

FirebaseDatabase firebaseDatabase;

ModelEvento evento;

ModelUsuario perfilOrganizador;

    NotificationBadge notificactionBadge;
    private Toolbar toolbar;
    FirebaseUser currentUser;
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

        // Obtener el ID del usuario actualmente logueado
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        GetNotifications(currentUser, notificactionBadge);

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

                    // Verificar si el usuario actual ya ha calificado este evento
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    boolean usuarioYaCalificado = false;
                    float calificacionUsuario = 0.0f;

                    if (evento.getCalificaciones() != null) {

                        for (ModelCalificacion calificacion : evento.getCalificaciones()) {
                            if (calificacion.getUserId().equals(userId)) {
                                usuarioYaCalificado = true;
                                calificacionUsuario = calificacion.getRating();
                                break;
                            }
                        }
                    }

                    // Deshabilitar el botón si el usuario ya ha calificado
                    if (usuarioYaCalificado) {
                        btn_agregarCalificacion.setEnabled(false);

                        // Establecer la calificación del usuario en la RatingBar y deshabilitarla
                        rb_userRating.setRating(calificacionUsuario);
                        rb_userRating.setIsIndicator(true);
                    }


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
                AlertDialog.Builder builder = new AlertDialog.Builder(CalificarActivity.this);
                builder.setMessage("¿Estás seguro de que quieres agregar la calificación?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Obtengo la calificación ingresada por el usuario
                                float calificacionUsuario = rb_userRating.getRating();

                                if (evento != null) {
                                    // *****Crea una nueva instancia de ModelCalificacion****

                                    // Obtengo al usuario actual que completó el evento
                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    ModelCalificacion nuevaCalificacion = new ModelCalificacion(userId, calificacionUsuario);

                                    // *****Modifico la calificación del organizador de eventos*****
                                    // Accede al nodo del perfil del usuario organizador del evento que completó el usuario actual
                                    DatabaseReference perfilRef = firebaseDatabase.getReference().child("Perfil").child(OrganizadorId);

                                    perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // Recupera el objeto ModelUsuario
                                                ModelUsuario modelUsuario = dataSnapshot.getValue(ModelUsuario.class);

                                                // Agrega la nueva calificación al objeto evento
                                                evento.agregarCalificacion(nuevaCalificacion);

                                                // Recalcula la calificación promedio y la setea en calificacionGeneral
                                                evento.calcularYSetearCalificacionPromedio();

                                                // Agrega la calificación del evento a la lista de calificaciones del usuario organizador
                                                float calificacionEvento = evento.getCalificacionGeneral();
                                                modelUsuario.agregarCalificacion(calificacionEvento);

                                                // Calcula y actualiza la calificación general del usuario
                                                modelUsuario.calcularYSetearCalificacionPromedio();

                                                //todo: ver donde realizar esto, si aqui o en la lista una vez que esta en el nodo completados
                                                // Agrega el evento completado al ModelUsuario()
                                                //modelUsuario.agregarEventoCompletado(idEvento);

                                                // Guarda el objeto ModelUsuario actualizado en el nodo del perfil (del organizador)
                                                perfilRef.setValue(modelUsuario);

                                                // Agrega el objeto a la base de datos
                                                firebaseDatabase.getReference().child("Eventos").child("Completados").child(idEvento).setValue(evento);

                                                // Muestra un Toast indicando que se agregó correctamente la calificación
                                                Toast.makeText(CalificarActivity.this, "Se agregó correctamente tu calificación", Toast.LENGTH_LONG).show();

                                                // Vuelvo a la SingleActivityEventoCompletado con la nueva calificacion general
                                                Intent intent = new Intent(CalificarActivity.this, ListaEventoCompletados.class);
                                                intent.putExtra("singleRating", evento.getCalificacionGeneral());
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Maneja el error de cancelación
                                        }
                                    });
                                } else {
                                    // Manejar el caso en el que el evento sea nulo
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // No hacer nada si el usuario selecciona "No"
                            }
                        })
                        .show();
            }
        });



    }//fin onCreate()

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
