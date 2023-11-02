package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.Utils.calcularTiempo;
import static com.example.sesionconfirebase.Utils.calcularVelocidad;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_test.DatosParticipacionEvento;
import com.example.gps_test.Ruta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaReportes extends AppCompatActivity {

    RecyclerView recyclerViewReportesEventos;
    ArrayList<ModelReporteAbandonosYFinalizados> recycleList;

    FirebaseDatabase firebaseDatabase;

    ReportesAdapter recyclerAdapter;

    TextView tvEventos,tvAbandonos,tvFinalizados;



    ArrayList<String>abandonosLista;
    ArrayList<String>finalizadosLista;

    ArrayList<ModelEstadistica> listaEstadisticasPorEvento;

    int totalParticipantesEvento;

    int totalAbandonos;
    int totalFinalizados;


    String userNameCustom;
    String nombreRuta;

    ModelEvento eventoCompletado;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reportes);

        // Tomo los ocntroles de la vista

        recyclerViewReportesEventos=findViewById(R.id.recyclerViewReportesEventos);
        tvEventos=findViewById(R.id.tvEventos);
        tvAbandonos=findViewById(R.id.tvAbandonos);
        tvFinalizados=findViewById(R.id.tvFinalizados);

        recycleList = new ArrayList<>();

        //Creo la instancia de la base de datos
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Creo una instancia del adapter
        recyclerAdapter = new ReportesAdapter(recycleList, ListaReportes.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewReportesEventos.setLayoutManager(linearLayoutManager);
        recyclerViewReportesEventos.addItemDecoration(new DividerItemDecoration(recyclerViewReportesEventos.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewReportesEventos.setNestedScrollingEnabled(false);
        recyclerViewReportesEventos.setAdapter(recyclerAdapter);




        // Obtener el ID del usuario actual
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Observar cambios en el nodo Completados
        DatabaseReference completadosRef = firebaseDatabase.getReference()
                .child("Eventos").child("Completados");

        // Inicializar variables para los totales
         totalAbandonos = 0;
         totalFinalizados = 0;

        completadosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Por cada evento completado
                for (DataSnapshot eventoSnapshot : dataSnapshot.getChildren()) {

                    ModelEvento eventoCompletado = eventoSnapshot.getValue(ModelEvento.class);

                    // Solo contemplo si ese evento es organizado por el usuario actual
                    if (eventoCompletado != null && eventoCompletado.getUserId().equals(userId)) {

                        // Obtengo la lista de participantes
                        ArrayList<String> listaParticipantes = eventoCompletado.getListaParticipantes();

                        if (listaParticipantes != null) {

                            // Inicializo la lista de estadísticas
                            ArrayList<ModelEstadistica> listaEstadisticas = new ArrayList<>();

                            // Armo una estadística por cada participante de ese evento...
                            for (String participante : listaParticipantes) {


                                DatabaseReference participanteRef = firebaseDatabase.getReference()
                                        .child("Events_Data").child(participante).child(eventoCompletado.getIdEvento());

                                participanteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            // Obtén el valor de abandono
                                            String abandono = dataSnapshot.child("abandono").getValue(String.class);

                                            DatosParticipacionEvento datosParticipacion = dataSnapshot.getValue(DatosParticipacionEvento.class);

                                            if (datosParticipacion != null) {
                                                String comienzo = datosParticipacion.getComienzo();
                                                String finalizacion = datosParticipacion.getFinalizacion();
                                                String distanciaCubierta = datosParticipacion.getDistanciaCubierta();

                                                String rutaId = eventoCompletado.getRuta();

                                                DatabaseReference rutaRef = firebaseDatabase.getReference()
                                                        .child("Route").child(rutaId);

                                                rutaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            Ruta ruta = dataSnapshot.getValue(Ruta.class);
                                                            String nombreRuta = ruta.getRouteName();

                                                            ModelEstadistica estadistica = new ModelEstadistica(
                                                                    eventoCompletado.getUserId(),
                                                                    eventoCompletado.getUserName(),
                                                                    eventoCompletado.getIdEvento(),
                                                                    eventoCompletado.getNombreEvento(),
                                                                    nombreRuta,
                                                                    participante,
                                                                    userNameCustom,
                                                                    eventoCompletado.getImagenEvento(),
                                                                    distanciaCubierta,
                                                                    calcularTiempo(comienzo, finalizacion),
                                                                    calcularVelocidad(distanciaCubierta, calcularTiempo(comienzo, finalizacion))
                                                            );

                                                            listaEstadisticas.add(estadistica);

                                                            // Verifica si es un abandono o finalizado y actualiza los totales
                                                            if (abandono.equals("Si")) {
                                                                totalAbandonos+=1;
                                                            } else {
                                                                totalFinalizados+=1;
                                                            }

                                                            // Si es el último participante, crea el reporte
                                                            if (listaEstadisticas.size() == listaParticipantes.size()) {
                                                                ModelReporteAbandonosYFinalizados reporte = new ModelReporteAbandonosYFinalizados(
                                                                        eventoCompletado.getUserId(),
                                                                        eventoCompletado.getUserName(),
                                                                        eventoCompletado.getIdEvento(),
                                                                        eventoCompletado.getNombreEvento(),
                                                                        nombreRuta,
                                                                        eventoCompletado.getImagenEvento(),
                                                                        totalAbandonos,
                                                                        totalFinalizados,
                                                                        listaEstadisticas.size()
                                                                );

                                                                reporte.setEstadisticas(listaEstadisticas);


                                                                // Agrega el objeto reporte a recycler
                                                                recycleList.add(reporte);
                                                                recyclerAdapter.notifyDataSetChanged();
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
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Manejar error de cancelación
                                    }
                                });
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar error de base de datos
            }
        });






    }//fin onCreate()






}//fin App
