package com.example.sesionconfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sesionconfirebase.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SingleReporteActivity extends AppCompatActivity {

    private String eventoId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_reporte);

        // Obtener el contenedor
        LinearLayout llContenedorEstadisticas = findViewById(R.id.llContenedorEstadisticas);


        // Obtener el eventoId del Intent
        Intent intent = getIntent();




        if (intent != null) {
            eventoId = intent.getStringExtra("EventoId");
            if (eventoId != null) {

                // Ahora uso el eventoId para buscar el reporte en la base de datos
                DatabaseReference reportesRef = FirebaseDatabase.getInstance().getReference().child("Reportes").child(eventoId);
                reportesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // El reporte existe en la base de datos
                            ModelReporteAbandonosYFinalizados reporte = dataSnapshot.getValue(ModelReporteAbandonosYFinalizados.class);

                            // Ahora puedo usar el reporte
                            if (reporte != null) {
                                List<ModelEstadistica> estadisticas = reporte.getEstadisticas();
                                if (estadisticas != null) {

                                    // Ordenar las estadísticas por tiempo (tiempo es una cadena en formato "hh:mm")
                                    Collections.sort(estadisticas, new Comparator<ModelEstadistica>() {
                                        @Override
                                        public int compare(ModelEstadistica estadistica1, ModelEstadistica estadistica2) {
                                            String tiempo1 = estadistica1.getTiempo();
                                            String tiempo2 = estadistica2.getTiempo();
                                            return tiempo1.compareTo(tiempo2);
                                        }
                                    });

                                    for (int i = 0; i < estadisticas.size(); i++) {
                                        ModelEstadistica estadistica = estadisticas.get(i);

                                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View itemView = inflater.inflate(R.layout.item_posicion_reporte, null);

                                        // Obtener las referencias a los elementos de la vista
                                        TextView tvPosicion = itemView.findViewById(R.id.tvPosicion);
                                        TextView tvNombreCompetidor = itemView.findViewById(R.id.tvNombreCompetidor);
                                        TextView tvDistancia = itemView.findViewById(R.id.tvDistancia);
                                        TextView tvVelocidad = itemView.findViewById(R.id.tvVelocidad);

                                        // Configurar los valores de los TextView con los datos de la estadística
                                        tvPosicion.setText((i + 1)); // La posición es 1-indexed
                                        tvNombreCompetidor.setText( estadistica.getUserName());
                                        tvDistancia.setText(estadistica.getDistanciaRecorrida());
                                        tvVelocidad.setText(estadistica.getVelocidadPromEvento());

                                        // Obtener el contenedor y agregar la vista inflada

                                        llContenedorEstadisticas.addView(itemView);


                                        // Agregar separador horizontal
                                        if (i < estadisticas.size() - 1) {
                                            View separator = new View(SingleReporteActivity.this);
                                            separator.setLayoutParams(new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                                    1)); // Establece la altura del separador a 1 pixel
                                            separator.setBackgroundColor(Color.rgb(94, 53, 177));

                                            llContenedorEstadisticas.addView(separator);
                                        }


                                    }//fin for(estadisticas)


                                    // Después de inflar y agregar los item_posicion_reporte, inflamos y agregamos el layout con el PieChart
                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View chart = inflater.inflate(R.layout.pie_chart_layout, null);

                                    // Agregar PieChart
                                    PieChart pieChart = chart.findViewById(R.id.pieChart);

                                    // Configurar el PieChart
                                    PieDataSet dataSet = new PieDataSet(reporte.getPieChartEntries(), "");
                                    dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    pieChart.setData(data);
                                    pieChart.getDescription().setEnabled(false);
                                    pieChart.setHoleRadius(25f);
                                    pieChart.setTransparentCircleRadius(30f);
                                    pieChart.animateY(1000, Easing.EaseInOutCubic);
                                    pieChart.invalidate();

                                    llContenedorEstadisticas.addView(chart);

                                }
                            }



                        } else {
                            // El reporte no existe en la base de datos

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar errores de base de datos
                    }
                });
            }

        }

        }//fin OnCreate


    }//fin App
