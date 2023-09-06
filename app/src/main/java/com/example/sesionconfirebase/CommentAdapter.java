package com.example.sesionconfirebase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<ModelComentario>list;
    Context context;

    private ModelComentario comentarioActual;

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

        // Almacena el comentario actual en la variable
        comentarioActual = comentario;

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
        // Agregar el listener al botón btn_reply
        holder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Comprueba si el comentarioActual no es nulo
                if (comentarioActual != null) {
                    // Crea un Intent para abrir la nueva actividad de respuesta
                    Intent intent = new Intent(context, RespuestaComentarioActivity.class);
                    Log.d("AdapterContext", "Context: " + context);


                    // Pasa los datos del comentario actual al Intent
                    intent.putExtra("comment", comentarioActual.getComment());
                    intent.putExtra("publisherId", comentarioActual.getPublisherId());
                    intent.putExtra("publisherName", comentarioActual.getPublisherName());
                    intent.putExtra("imagenPerfilUri", comentarioActual.getImagen_perfil());
                    intent.putExtra("commentId", comentarioActual.getCommentId());
                    intent.putExtra("idEvento", comentarioActual.getEventoId());

                    // Inicia la nueva actividad
                    context.startActivity(intent);
                }
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
