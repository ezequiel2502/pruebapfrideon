package com.example.sesionconfirebase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gps_test.ui.ActivityBuscarEventosRecycler.ModelEvento;
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

import java.util.ArrayList;

public class ReportesAdapter extends RecyclerView.Adapter<ReportesAdapter.ViewHolder>{


    ArrayList<ModelReporteAbandonosYFinalizados> list;
    Context context;

    public ReportesAdapter(ArrayList<ModelReporteAbandonosYFinalizados> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_reporte_evento,parent,false);
        return new ReportesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportesAdapter.ViewHolder holder, int position) {


        // Obtener el reporte en esta posición
        ModelReporteAbandonosYFinalizados reporte=list.get(position);


        // Configurar el PieChart
        PieDataSet dataSet = new PieDataSet(reporte.getPieChartEntries(), "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        holder.pieChart.setData(data);
        holder.pieChart.getDescription().setEnabled(false);
        holder.pieChart.setHoleRadius(25f);
        holder.pieChart.setTransparentCircleRadius(30f);
        holder.pieChart.animateY(1000, Easing.EaseInOutCubic);
        holder.pieChart.invalidate();

        //***Cargamos el item-reporte

        // Cargar la imagen usando Glide
       Glide.with(context)
               .load(reporte.getImageUrl()) //  para obtener la URL de la imagen
               .into(holder.imvEvento);


        //cargamos el resto de controles
        holder.tv_tituloEvento.setText(reporte.getNombreEvento());
        holder.tv_UserNameOrganizador.setText(reporte.getOrganizadorUsername());
        holder.tv_Ruta.setText(reporte.getNombreRuta());
        holder.tv_Abandonos.setText(String.valueOf(reporte.getTotalAbandonos()));
        holder.tv_Finalizados.setText(String.valueOf(reporte.getTotalFinalizados()));
        holder.tv_Participantes.setText( String.valueOf(reporte.getTotalParticipantes()));


        //Agrego un listener para cuando cliquee sobre el reporte,
        // me lleve a los detalles que permiten imprimir el reporte(crear Pdf)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, SingleReporteActivity.class);
                intent.putExtra("EventoId",reporte.getEventoId());
                intent.putExtra("NombreEvento",reporte.getNombreEvento());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.imvEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference events = database.getReference().child("Eventos").child("Completados").child(reporte.getEventoId());
                events.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()) {
                            ModelEvento evento = snapshot.getValue(ModelEvento.class);

                            Intent intent = new Intent(context, SingleEventoPostuladosActivity.class);
                            intent.putExtra("singleImage", evento.getImagenEvento());
                            intent.putExtra("singleEvento", evento.getNombreEvento());
                            intent.putExtra("singleRuta", evento.getRuta());
                            intent.putExtra("singleDescripcion", evento.getDescripcion());
                            intent.putExtra("singleFechaEncuentro", evento.getFechaEncuentro());
                            intent.putExtra("singleHoraEncuentro", evento.getHoraEncuentro());
                            intent.putExtra("singleCupoMinimo", evento.getCupoMinimo());
                            intent.putExtra("singleCupoMaximo", evento.getCupoMaximo());
                            intent.putExtra("singleCategoria", evento.getCategoria());
                            intent.putExtra("singleUserName", evento.getUserName());
                            intent.putExtra("singleUserId", evento.getUserId());
                            //intent.putExtra("singleRating",evento.getRating());
                            intent.putExtra("singlePublicoPrivado", evento.getPublicoPrivado());
                            intent.putExtra("singleActivarDesactivar", evento.getActivadoDescativado());
                            intent.putExtra("EventoId", evento.getIdEvento());
                            intent.putExtra("isReportInstance", "True");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


    }

    @Override
    public int getItemCount()  {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        PieChart pieChart;
        ImageView imvEvento;
        TextView tv_tituloEvento,tv_UserNameOrganizador,tv_Ruta;
        TextView tv_Abandonos,tv_Finalizados,tv_Participantes;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            //Controles del item_evento_estadistica

            tv_tituloEvento=itemView.findViewById(R.id.tv_tituloEvento);
            tv_UserNameOrganizador=itemView.findViewById(R.id.tv_UserNameOrganizador);
            tv_Ruta=itemView.findViewById(R.id.tv_Ruta);
            tv_Abandonos=itemView.findViewById(R.id.tv_Abandonos);
            tv_Finalizados=itemView.findViewById(R.id.tv_Finalizados);
            tv_Participantes=itemView.findViewById(R.id.tv_Participantes);
            imvEvento=itemView.findViewById(R.id.imvEvento);
            pieChart=itemView.findViewById(R.id.pieChart);






        }
    }
}
