package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.Utils.calcularTiempo;
import static com.example.sesionconfirebase.Utils.calcularVelocidad;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_test.DatosParticipacionEvento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaEstadisticas extends AppCompatActivity {



    RecyclerView recyclerViewEstadisticasEventos;
    ArrayList<ModelEstadistica> recycleList;

    FirebaseDatabase firebaseDatabase;

    EstadisticasAdapter recyclerAdapter;

    String userNameCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_estadisticas);

        // Tomo los ocntroles de la vista

        recyclerViewEstadisticasEventos=findViewById(R.id.recyclerViewEstadisticasEventos);
        recycleList = new ArrayList<>();

        //Creo la instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Creo una instancia del adapter
        recyclerAdapter = new EstadisticasAdapter(recycleList, ListaEstadisticas.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewEstadisticasEventos.setLayoutManager(linearLayoutManager);
        recyclerViewEstadisticasEventos.addItemDecoration(new DividerItemDecoration(recyclerViewEstadisticasEventos.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewEstadisticasEventos.setNestedScrollingEnabled(false);
        recyclerViewEstadisticasEventos.setAdapter(recyclerAdapter);



        //-----Accedo al perfil del usuario participante-------

        // Obtener el ID del usuario actualmente logueado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Acceder al nodo de "Perfil" del usuario
        DatabaseReference perfilRef = firebaseDatabase.getReference().child("Perfil").child(userId);

        perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Recupera el objeto ModelUsuario
                    ModelUsuario modelUsuario = dataSnapshot.getValue(ModelUsuario.class);

                    if (modelUsuario != null) {
                        // Obtiene el userNameCustom
                         userNameCustom = modelUsuario.getUserNameCustom();



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Maneja el error de cancelación
            }
        });

        //-----Accedo al perfil del usuario participante-------



        //*****Observo cambios en Events_data

        DatabaseReference eventsDataRef = firebaseDatabase.getReference().child("Events_Data").child(userId);

        eventsDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventoSnapshot : dataSnapshot.getChildren()) {

                    //Tomo la key del evento que me va a servir para buscarlo en el nodo de completados
                    String eventoId = eventoSnapshot.getKey();

                    //Recojo todos los datos de finalizacion para armar la estadistica
                    DatabaseReference datosParticipacionRef = firebaseDatabase.getReference().child("Events_Data").child(userId).child(eventoId);

                    datosParticipacionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                //Datos de participacion
                                DatosParticipacionEvento datosParticipacion = dataSnapshot.getValue(DatosParticipacionEvento.class);
                                if (datosParticipacion != null) {

                                    String comienzo = datosParticipacion.getComienzo();
                                    String finalizacion = datosParticipacion.getFinalizacion();
                                    String distanciaCubierta = datosParticipacion.getDistanciaCubierta();


                                    //Ahora entro en el nodo de completados para obtener el resto de datos para armar la estadistica
                                    DatabaseReference eventosCompletadosRef = firebaseDatabase.getReference().child("Eventos").child("Completados").child(eventoId);

                                    eventosCompletadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot eventoSnapshot) {
                                            if (eventoSnapshot.exists()) {


                                                //Datos del evento completado
                                                ModelEvento evento = eventoSnapshot.getValue(ModelEvento.class);

                                                // Aquí se usa comienzo, finalizacion, distanciaCubierta y evento para crear ModelEstadistica
                                                ModelEstadistica estadistica = new ModelEstadistica(
                                                        evento.getUserId(),//del organizador
                                                        evento.getUserName(),//del organizador
                                                        eventoId,
                                                        evento.getNombreEvento(),
                                                        userId,//del usuario que participo
                                                        userNameCustom,
                                                        evento.getImagenEvento(), // No tengo acceso a este dato, reemplázalo con la URL de la imagen
                                                        distanciaCubierta,

                                                        calcularTiempo(comienzo,finalizacion),
                                                        calcularVelocidad(distanciaCubierta,calcularTiempo(comienzo,finalizacion))//metodo de la clase static Utils
                                                );

                                                // Agrega estadistica a tu lista
                                                recycleList.add(estadistica);
                                                recyclerAdapter.notifyDataSetChanged();
                                            }
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });


        //*****Observo cambios en Events_data

    }//fin onCReate()


}
