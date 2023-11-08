package com.example.sesionconfirebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

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
//import com.itextpdf.kernel.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.graphics.pdf.PdfDocument;
import android.widget.Toast;


public class SingleReporteActivity extends AppCompatActivity {

    private String eventoId;
    private String nombreEvento;
    LinearLayout llContenedorEstadisticas;
    CardView cardViewImpresion;
    private static final int REQUEST_CODE_SAVE_PDF = 1;
    private PdfDocument document;

    TextView tvSubtitulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_reporte);

        // Obtener el Intent desde el adapter
        Intent intent = getIntent();

        // Obtener el contenedor
        llContenedorEstadisticas = findViewById(R.id.llContenedorEstadisticas);

        //Otros controles de la vista
        tvSubtitulo = findViewById(R.id.tvSubtitulo);


        //Obtengo el boton de impresion
        cardViewImpresion=findViewById(R.id.cardViewImpresion);

        // Inflar la vista del título y el logo
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tituloAppView = inflater.inflate(R.layout.layout_titulo_app, null);
        llContenedorEstadisticas.addView(tituloAppView);

        // Obtener una referencia al TextView dentro de la vista inflada
        TextView tv_tituloEvento = tituloAppView.findViewById(R.id.tv_tituloEvento);

        // Establecer el texto para el titulo del evento
        tv_tituloEvento.setText("Evento: "+intent.getStringExtra("NombreEvento"));





        //Busco en la base de datos el reporte usando el EventoId
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

                                tvSubtitulo.setText(reporte.getNombreEvento());

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
                                        TextView tvTiempo = itemView.findViewById(R.id.tvTiempo);
                                        TextView tvAbandono = itemView.findViewById(R.id.tvAbandono);

                                        // Configurar los valores de los TextView con los datos de la estadística
                                        tvPosicion.setText(String.valueOf(i + 1)); // La posición es 1-indexed
                                        tvNombreCompetidor.setText( estadistica.getUserName());
                                        tvDistancia.setText(estadistica.getDistanciaRecorrida());
                                        tvVelocidad.setText(estadistica.getVelocidadPromEvento());
                                        tvTiempo.setText(estadistica.getTiempo());
                                        tvAbandono.setText(estadistica.getAbandonoSIoNO());

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


//                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                                    View itemView = inflater.inflate(R.layout.item_posicion_reporte, null);


                                    // Después de inflar y agregar los item_posicion_reporte, inflamos y agregamos el layout con el PieChart
                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View chart = inflater.inflate(R.layout.pie_chart_layout, null);


                                    //Tomo los controles del layout del piechart
                                    TextView tv_Abandonos = chart.findViewById(R.id.tv_Abandonos);
                                    TextView tv_Finalizados = chart.findViewById(R.id.tv_Finalizados);
                                    TextView tv_Participantes = chart.findViewById(R.id.tv_Participantes);

                                    tv_Abandonos.setText(String.valueOf(reporte.getTotalAbandonos()));
                                    tv_Finalizados.setText(String.valueOf(reporte.getTotalFinalizados()));
                                    tv_Participantes.setText(String.valueOf(reporte.getTotalParticipantes()));

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


        //Boton para imprimir el reporte
        cardViewImpresion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createPdf();
            }
        });




    }//fin OnCreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SAVE_PDF && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    document.writeTo(outputStream);
                    document.close();
                    outputStream.close();
                    Toast.makeText(this, "Documento PDF creado y guardado con éxito", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al guardar el PDF", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void createPdf() {
        // Crear un nuevo documento PDF
        document = new PdfDocument();

        // Configurar el tamaño de la página
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(llContenedorEstadisticas.getWidth(), llContenedorEstadisticas.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Dibujar la vista en la página PDF
        llContenedorEstadisticas.draw(page.getCanvas());

        // Terminar la página
        document.finishPage(page);

        // Crear un Intent para la acción de guardar
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "reporte.pdf");

        startActivityForResult(intent, REQUEST_CODE_SAVE_PDF);
    }







}//fin App
