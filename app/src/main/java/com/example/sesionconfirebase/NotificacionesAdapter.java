package com.example.sesionconfirebase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gps_test.BuscarEventosMapaActivity;
import com.example.gps_test.Ruta;
import com.example.gps_test.ui.map.TupleDouble;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.ViewHolderNotificacion>  {

    ArrayList <ModelNotificacion> list;

    Context context;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public NotificacionesAdapter(ArrayList<ModelNotificacion> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificacionesAdapter.ViewHolderNotificacion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_notificacion,parent,false);
        return new ViewHolderNotificacion(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionesAdapter.ViewHolderNotificacion holder, int position) {
        ModelNotificacion notificacion = list.get(position);
        String mensaje = notificacion.getTitulo() + " " + notificacion.getDetalle();
        holder.tv_MensajeNotificacion.setText(mensaje);
        holder.tv_TipoNotificacion.setText(notificacion.getTipoNotificacion());
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderNotificacion extends RecyclerView.ViewHolder {

        //Son los controles del itemEvento
        TextView tv_TipoNotificacion,tv_NombreUsuario,tv_MensajeNotificacion;

        public ViewHolderNotificacion(@NonNull View itemView) {
            super(itemView);
            tv_TipoNotificacion=itemView.findViewById(R.id.tv_TipoNotificacion);
            tv_MensajeNotificacion=itemView.findViewById(R.id.tv_MensajeNotificacion);

        }
    }
}
