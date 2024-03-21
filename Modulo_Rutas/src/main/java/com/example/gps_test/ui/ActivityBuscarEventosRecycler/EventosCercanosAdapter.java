package com.example.gps_test.ui.ActivityBuscarEventosRecycler;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gps_test.R;
import com.example.gps_test.Ruta;
import com.example.gps_test.SingleEventoPublicoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class EventosCercanosAdapter extends RecyclerView.Adapter<EventosCercanosAdapter.ViewHolder> {

    ArrayList <ModelEvento> list;

    Context context;

    public EventosCercanosAdapter(ArrayList<ModelEvento> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public EventosCercanosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_list_events,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventosCercanosAdapter.ViewHolder holder, int position) {
        ModelEvento evento = list.get(position);

        //Cargo el itemEventoPublicoVigente

        // Cargar la imagen usando Glide
        Glide.with(context)
                .load(evento.getImagenEvento()) // ModelEvento tiene un método para obtener la URL de la imagen
                .into(holder.imvEvento); // Coloca la imagen en el Imag

        holder.tv_tituloEvento.setText(evento.getNombreEvento());
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

        FirebaseDatabase Database = FirebaseDatabase.getInstance();
        DatabaseReference rutaRef = Database.getReference()
                .child("Route").child(evento.getRuta());
        rutaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Ruta ruta = snapshot.getValue(Ruta.class);
                    holder.tv_Ruta.setText(ruta.routeName.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.tv_Ruta.setText(evento.getRuta());
            }
        });

        //Agrego un Listener para cuando cliquee sobre el evento(item), me lleva a los detalles de la publicacion para postularme
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, SingleEventoPublicoActivity.class);
                intent.putExtra("singleImage",evento.getImagenEvento());
                intent.putExtra("singleEvento",evento.getNombreEvento());
                intent.putExtra("singleRuta",evento.getRuta());
                intent.putExtra("singleDescripcion",evento.getDescripcion());
                intent.putExtra("singleFechaEncuentro",evento.getFechaEncuentro());
                intent.putExtra("singleHoraEncuentro",evento.getHoraEncuentro());
                intent.putExtra("singleCupoMinimo",evento.getCupoMinimo());
                intent.putExtra("singleCupoMaximo",evento.getCupoMaximo());
                intent.putExtra("singleCategoria",evento.getCategoria());
                intent.putExtra("singleUserName",evento.getUserName());
                intent.putExtra("singleUserId",evento.getUserId());
                intent.putExtra("singleRating",evento.getRating());
                intent.putExtra("singlePublicoPrivado",evento.getPublicoPrivado());
                intent.putExtra("singleActivarDesactivar",evento.getActivadoDescativado());
                intent.putExtra("EventoId",evento.getIdEvento());
                intent.putExtra("TokenFCM",evento.getTokenFCM());
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });



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

    public void ordenarListaPorFecha() {
        Collections.sort(list, new Comparator<ModelEvento>() {
            @Override
            public int compare(ModelEvento evento1, ModelEvento evento2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                try {
                    Date date1 = dateFormat.parse(evento1.getFechaEncuentro());
                    Date date2 = dateFormat.parse(evento2.getFechaEncuentro());
                    return date1.compareTo(date2); // Compara en orden ascendente (más antiguo primero)
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        notifyDataSetChanged();
    }
}
