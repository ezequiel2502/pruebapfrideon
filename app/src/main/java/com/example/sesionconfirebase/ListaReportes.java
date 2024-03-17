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

                                String estadisticaId = eventoCompletado.getIdEvento() + "_" + participante; // Crear un ID único para cada estadística

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference estadisticasRef = database.getReference("Estadisticas");
                                final ModelEstadistica[] estadistica = new ModelEstadistica[1];
                                estadisticasRef.child(estadisticaId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // La estadística ya existe, puedes recuperarla si es necesario
                                            estadistica[0] = dataSnapshot.getValue(ModelEstadistica.class);
                                            listaEstadisticas.add(estadistica[0]);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Manejar errores de base de datos si es necesario
                                    }
                                });
                                if(estadistica[0]!=null)
                                {
                                    if(estadistica[0].getAbandonoSIoNO().equals("Si"))
                                    {
                                        totalAbandonosParcial[0]++;
                                    }else{
                                        totalFinalizadosParcial[0]++;
                                    }
                                }
                            }//fin for(listaParticipantes)
                            totalAbandonos+= totalAbandonosParcial[0];
                            totalFinalizados+=totalFinalizadosParcial[0];
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
