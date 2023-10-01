package com.example.sesionconfirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EstadisticasAdapter extends RecyclerView.Adapter<EstadisticasAdapter.ViewHolder>{


    ArrayList<ModelEstadistica> list;
    Context context;


    public EstadisticasAdapter(ArrayList<ModelEstadistica> list, Context context) {
        this.list = list;
        this.context = context;
    }
    @NonNull
    @Override
    public EstadisticasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_estadistica_evento,parent,false);
        return new EstadisticasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EstadisticasAdapter.ViewHolder holder, int position) {

        //Cargo el item_estadistica_evento


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_tituloEvento,tv_UserNameOrganizador,tv_Ruta;
        TextView tv_Tiempo,tv_Distancia,tv_Velocidad;

        ImageView imvEvento;
        RatingBar rb_calificacionEvento;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            //Controles del item_evento_estadistica

            tv_tituloEvento=itemView.findViewById(R.id.tv_tituloEvento);
            tv_UserNameOrganizador=itemView.findViewById(R.id.tv_UserNameOrganizador);
            tv_Ruta=itemView.findViewById(R.id.tv_Ruta);
            tv_Tiempo=itemView.findViewById(R.id.tv_Tiempo);
            tv_Distancia=itemView.findViewById(R.id.tv_Distancia);
            tv_Velocidad=itemView.findViewById(R.id.tv_Velocidad);
            rb_calificacionEvento=itemView.findViewById(R.id.rb_calificacionEvento);
            imvEvento=itemView.findViewById(R.id.imvEvento);


        }
    }
}
