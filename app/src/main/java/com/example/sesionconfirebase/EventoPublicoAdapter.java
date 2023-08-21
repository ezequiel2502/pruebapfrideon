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

public class EventoPublicoAdapter extends RecyclerView.Adapter<EventoPublicoAdapter.ViewHolder> {

    ArrayList <ModelEvento> list;

    Context context;

    public EventoPublicoAdapter(ArrayList<ModelEvento> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public EventoPublicoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_evento,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoPublicoAdapter.ViewHolder holder, int position) {
        ModelEvento evento = list.get(position);

        // Cargar la imagen usando Glide
        Glide.with(context)
                .load(evento.getImagenEvento()) // ModelEvento tiene un método para obtener la URL de la imagen
                .into(holder.imvEvento); // Coloca la imagen en el Imag

        holder.tv_tituloEvento.setText(evento.getNombreEvento());
        holder.tv_Ruta.setText(evento.getRuta());
        holder.tv_Descripcion.setText(evento.getDescripcion());
        holder.tv_FechaEncuentro.setText(evento.getFechaEncuentro());
        holder.tv_HoraEncuentro.setText(evento.getHoraEncuentro());
        holder.tv_CupoMinimo.setText(evento.getCupoMinimo());
        holder.tv_CupoMaximo.setText(evento.getCupoMaximo());
        holder.tv_Categoria.setText(evento.getCategoria());
        holder.tv_UserName.setText(evento.getUserName());
        holder.tv_UserId.setText(evento.getUserId());
        holder.tv_ActivarDescativar.setText(evento.getActivadoDescativado());
        holder.tv_PublicoPrivado.setText(evento.getPublicoPrivado());
        holder.rb_calificacionEvento.setRating(evento.getRating());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Son los controles del itemEvento
        TextView tv_tituloEvento,tv_Ruta,tv_Descripcion,tv_FechaEncuentro,tv_HoraEncuentro,
                tv_CupoMinimo,tv_CupoMaximo,tv_Categoria,tv_UserName,tv_UserId,tv_PublicoPrivado,tv_ActivarDescativar;
        ImageView imvEvento;

        RatingBar rb_calificacionEvento;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Son los controles del itemEvento
            tv_tituloEvento=itemView.findViewById(R.id.tv_tituloEvento);
            tv_Ruta=itemView.findViewById(R.id.tv_Ruta);
            tv_Descripcion=itemView.findViewById(R.id.tv_Descripcion);
            tv_FechaEncuentro=itemView.findViewById(R.id.tv_FechaEncuentro);
            tv_HoraEncuentro=itemView.findViewById(R.id.tv_HoraEncuentro);
            tv_CupoMinimo=itemView.findViewById(R.id.tv_CupoMinimo);
            tv_CupoMaximo=itemView.findViewById(R.id.tv_CupoMaximo);
            tv_Categoria=itemView.findViewById(R.id.tv_Categoria);
            tv_UserName=itemView.findViewById(R.id.tv_UserName);
            tv_UserId=itemView.findViewById(R.id.tv_UserId);
            rb_calificacionEvento=itemView.findViewById(R.id.rb_calificacionEvento);
            tv_PublicoPrivado=itemView.findViewById(R.id.tv_PublicoPrivado);
            tv_ActivarDescativar=itemView.findViewById(R.id.tv_ActivarDescativar);
            imvEvento=itemView.findViewById(R.id.imvEvento);

        }
    }
}
