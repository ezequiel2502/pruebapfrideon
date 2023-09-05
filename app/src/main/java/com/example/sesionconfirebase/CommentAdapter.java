package com.example.sesionconfirebase;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<ModelComentario>list;
    Context context;

    public CommentAdapter(ArrayList<ModelComentario> list, Context context) {
        this.list = list;
        this.context = context;
    }


    //Para representar cada elemento de la lista de comentarios
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_comentario,parent,false);
        return new ViewHolder(view);
    }


    //Este método se llama para vincular datos a una instancia de ViewHolder.
    // En este caso, se cargan los datos de un objeto ModelEvento
    // en los elementos de la vista correspondientes (como ImageView, TextView, etc.)
    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {

        ModelComentario comentario=list.get(position);

        //Se carga el itemComentario

        // Cargar la imagen usando Glide desde la URI almacenada en la base de datos
        String imagePath = comentario.getImagen_perfil();
        Glide.with(context)
                .load(imagePath) // Carga la imagen desde la URI
                .into(holder.image_profile);

        //Se cargan el resto de campos
        holder.tv_comment.setText(comentario.getComment());
        holder.tv_userName.setText(comentario.getPublisherName());

        //Agrego comportamiento a los botones

        //Boton Reponder
        holder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Boton Eliminar
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_userName,tv_comment;

        Button btn_reply,btn_delete;
        ImageView image_profile;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Controles del itemComentario
            tv_userName=itemView.findViewById(R.id.tv_userName);
            tv_comment=itemView.findViewById(R.id.tv_comment);
            image_profile=itemView.findViewById(R.id.image_profile);
            btn_reply=itemView.findViewById(R.id.btn_reply);
            btn_delete=itemView.findViewById(R.id.btn_delete);


        }
    }
}
