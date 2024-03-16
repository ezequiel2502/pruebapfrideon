package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.Utils.calcularTiempo;
import static com.example.sesionconfirebase.Utils.calcularTiempo2;
import static com.example.sesionconfirebase.Utils.calcularVelocidad;
import static com.example.sesionconfirebase.Utils.calcularVelocidad2;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gps_test.DatosParticipacionEvento;
import com.example.gps_test.Ruta;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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

    TextView tvEventos, tvAbandonos, tvFinalizados, tvParticipantes;

    PieChart pieChart;
    ArrayList<PieEntry> entries;
    int totalAbandonos;
    int totalFinalizados;

    String userNameCustom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reportes);

        // Tomo los ocntroles de la vista

        recyclerViewReportesEventos=findViewById(R.id.recyclerViewReportesEventos);
        tvEventos=findViewById(R.id.tvEventos);
        tvAbandonos=findViewById(R.id.tvAbandonos);
        tvFinalizados=findViewById(R.id.tvFinalizados);
        tvParticipantes=findViewById(R.id.tvParticipantes);
        pieChart=findViewById(R.id.pieChart);





        //creo la lista para el recycler
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
                            final int[] totalAbandonosParcial = {0};
                            final int[] totalFinalizadosParcial = {0};
                            final boolean[] duplicating = {false};
                            final ModelReporteAbandonosYFinalizados[] previous = {null};
                            for (String participante : listaParticipantes) {

                                DatabaseReference participanteRef = firebaseDatabase.getReference()
                                        .child("Events_Data").child(participante).child(eventoCompletado.getIdEvento());

                                participanteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            //Pregunto si es un abandono
                                            String abandono = dataSnapshot.child("abandono").getValue(String.class);

                                            //Obtengo los otros datos departicipacion
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

                                                            // Acceder al nodo de "Perfil" del participante
                                                            DatabaseReference perfilRef = firebaseDatabase.getReference().child("Perfil").child(participante);

                                                            perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        ModelUsuario modelUsuario = dataSnapshot.getValue(ModelUsuario.class);

                                                                        if (modelUsuario != null) {
                                                                            String userNameCustom = modelUsuario.getUserNameCustom();
                                                                            final ModelEstadistica[] existingEstadistica = new ModelEstadistica[1];
                                                                            String estadisticaId = eventoCompletado.getIdEvento() + "_" + participante; // Crear un ID único para cada estadística

                                                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                            DatabaseReference estadisticasRef = database.getReference("Estadisticas");

                                                                            estadisticasRef.child(estadisticaId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                    if (dataSnapshot.exists()) {
                                                                                        // La estadística ya existe, puedes recuperarla si es necesario
                                                                                        existingEstadistica[0] = dataSnapshot.getValue(ModelEstadistica.class);
                                                                                        existingEstadistica[0].setAbandonoSIoNO(abandono);

                                                                                        listaEstadisticas.add(existingEstadistica[0]);

                                                                                        // Verifica si es un abandono o finalizado y actualiza los totales
                                                                                        if (abandono.equals("Si")) {
                                                                                            totalAbandonos += 1;
                                                                                            totalAbandonosParcial[0] += 1;
                                                                                        } else {
                                                                                            totalFinalizados += 1;
                                                                                            totalFinalizadosParcial[0] += 1;
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {
                                                                                    // Manejar errores de base de datos si es necesario
                                                                                }
                                                                            });



                                                                            // Si es el último participante, crea el reporte
                                                                            //if (listaEstadisticas.size() == listaParticipantes.size()) {
                                                                                if (duplicating[0] == true)
                                                                                {
                                                                                    recycleList.remove(previous[0]);
                                                                                }
                                                                                ModelReporteAbandonosYFinalizados reporte = new ModelReporteAbandonosYFinalizados(
                                                                                        eventoCompletado.getUserId(),
                                                                                        eventoCompletado.getUserName(),
                                                                                        eventoCompletado.getIdEvento(),
                                                                                        eventoCompletado.getNombreEvento(),
                                                                                        nombreRuta,
                                                                                        eventoCompletado.getImagenEvento(),
                                                                                        totalAbandonosParcial[0],
                                                                                        totalFinalizadosParcial[0],
                                                                                        listaEstadisticas.size()
                                                                                );

                                                                                reporte.setEstadisticas(listaEstadisticas);

                                                                                // Agrega el objeto reporte a recycler
                                                                                recycleList.add(reporte);
                                                                                recyclerAdapter.notifyDataSetChanged();

                                                                                //totalAbandonosParcial[0] = 0;
                                                                                //totalFinalizadosParcial[0] = 0;

                                                                                //Guardo el reporte en un nodo nuevo llamado Reportes
                                                                                DatabaseReference reportesRef = firebaseDatabase.getReference().child("Reportes").child(eventoCompletado.getIdEvento());
                                                                                reportesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                        if (dataSnapshot.exists()) {
                                                                                            // El reporte ya existe, entonces lo sobreescribo
                                                                                            reportesRef.setValue(reporte);
                                                                                        } else {
                                                                                            // El reporte no existe, créalo
                                                                                            reportesRef.setValue(reporte);
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                                        // Manejar errores
                                                                                    }
                                                                                });

                                                                                //Para calcular los totales
                                                                                calcularTotales();

                                                                                // Configurar el PieChart, para los totales
                                                                                PieDataSet dataSet = new PieDataSet(generarDatosParaPieChart(), "");
                                                                                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                                                                                dataSet.setValueTextColor(Color.BLACK);
                                                                                dataSet.setValueTextSize(12f);

                                                                                PieData data = new PieData(dataSet);

                                                                                pieChart.setData(data);
                                                                                pieChart.getDescription().setEnabled(false);
                                                                                pieChart.animateY(1000, Easing.EaseInOutCubic);
                                                                                pieChart.setEntryLabelColor(Color.BLACK);
                                                                            //}
                                                                            duplicating[0] = true;
                                                                            previous[0] = reporte;
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    // Manejar error de cancelación
                                                                    String rutaId = eventoCompletado.getRuta();
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        // Manejar error de cancelación
                                                        String rutaId = eventoCompletado.getRuta();
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

                            }//fin for(listaParticipantes)
                        }
                    }
                }//fin for(evento completados)

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar error de base de datos
            }
        });


    }//fin onCreate()



    private void calcularTotales() {
        int totalParticipantes = 0;
        int totalAbandonos = 0;
        int totalFinalizados = 0;
        int totalEventos = 0;

        // Iterar a través de la lista de reportes
        for (ModelReporteAbandonosYFinalizados reporte : recycleList) {
            totalParticipantes += reporte.getTotalParticipantes();
            totalAbandonos += reporte.getTotalAbandonos();
            totalFinalizados += reporte.getTotalFinalizados();
        }

        // Ahora, fuera del bucle, calculamos el total de eventos
        totalEventos = recycleList.size();

        // Mostrar los resultados
        tvEventos.setText(String.valueOf(totalEventos));
        tvParticipantes.setText(String.valueOf(totalParticipantes));
        tvAbandonos.setText(String.valueOf(totalAbandonos));
        tvFinalizados.setText(String.valueOf(totalFinalizados));
    }

    private ArrayList<PieEntry> generarDatosParaPieChart() {


        // Inicializar la lista de entradas
        entries = new ArrayList<>();

        float porcentajeAbandonos = calcularPorcentajeAbandonos();
        float porcentajeFinalizados = calcularPorcentajeFinalizados();

        if (porcentajeAbandonos > 0) {
            entries.add(new PieEntry(porcentajeAbandonos, "Abandonos"));
        }

        if (porcentajeFinalizados > 0) {
            entries.add(new PieEntry(porcentajeFinalizados, "Finalizados"));
        }

        return entries;
    }



    private float calcularPorcentajeAbandonos() {

        int totalParticipantes = 0;
        int totalAbandonos = 0;

        // Iterar a través de la lista de reportes
        for (ModelReporteAbandonosYFinalizados reporte : recycleList) {
            totalParticipantes += reporte.getTotalParticipantes();
            totalAbandonos += reporte.getTotalAbandonos();
        }

        if (totalParticipantes > 0) {
            return ((float) totalAbandonos / totalParticipantes) * 100;
        } else {
            return 0;
        }
    }

    private float calcularPorcentajeFinalizados() {

        int totalParticipantes = 0;
        int totalFinalizados = 0;


        // Iterar a través de la lista de reportes
        for (ModelReporteAbandonosYFinalizados reporte : recycleList) {
            totalParticipantes += reporte.getTotalParticipantes();
            totalFinalizados += reporte.getTotalFinalizados();
        }
        if (totalParticipantes > 0) {
            return ((float) totalFinalizados / totalParticipantes) * 100;
        } else {
            return 0;
        }
    }




}//fin App
