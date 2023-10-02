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

import com.bumptech.glide.Glide;

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

        ModelEstadistica estadistica=list.get(position);

        //***Cargamos el item-estadistica


        // Cargar la imagen usando Glide
        Glide.with(context)
                .load(estadistica.getImageUrl()) //  para obtener la URL de la imagen
                .into(holder.imvEvento);
        //cargamos el resto de controles
        holder.tv_tituloEvento.setText(estadistica.getNombreEvento());
        holder.tv_UserNameOrganizador.setText(estadistica.getOrganizadorUsername());
        holder.tv_Ruta.setText("Ruta");//revisar esto
        holder.tv_Tiempo.setText(estadistica.getTiempo());
        holder.tv_Distancia.setText(estadistica.getDistanciaRecorrida());
        holder.tv_Velocidad.setText(estadistica.getVelocidadPromEvento());






    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_tituloEvento,tv_UserNameOrganizador,tv_Ruta;
        TextView tv_Tiempo,tv_Distancia,tv_Velocidad;

        ImageView imvEvento;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            //Controles del item_evento_estadistica

            tv_tituloEvento=itemView.findViewById(R.id.tv_tituloEvento);
            tv_UserNameOrganizador=itemView.findViewById(R.id.tv_UserNameOrganizador);
            tv_Ruta=itemView.findViewById(R.id.tv_Ruta);
            tv_Tiempo=itemView.findViewById(R.id.tv_Tiempo);
            tv_Distancia=itemView.findViewById(R.id.tv_Distancia);
            tv_Velocidad=itemView.findViewById(R.id.tv_Velocidad);
            imvEvento=itemView.findViewById(R.id.imvEvento);


        }
    }
}
