package com.example.sesionconfirebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class CommentAdapterPostulados extends RecyclerView.Adapter<CommentAdapterPostulados.ViewHolder> {


    ArrayList<ModelComentario>list;
    Context context;
    String userId;

    //**************Interfaz para eliminar respuesta del lado de la SingleEventoPublicoActivity
    private OnResponseDeleteListener onResponseDeleteListener;


    public interface OnResponseDeleteListener {
        void onResponseDelete(ModelRespuestaComentario respuesta);
    }

    public void setOnResponseDeleteListener(OnResponseDeleteListener listener) {
        this.onResponseDeleteListener = listener;
    }
    //*******************************************************************************************

    //                                  *****^****

    //**************Interfaz para eliminar comentario del lado de la SingleEventoPublicoActivity
    private OnCommentDeleteListener onCommentDeleteListener;

    public interface OnCommentDeleteListener {
        void onCommentDelete(ModelComentario comentarioAEliminar);
    }

    public void setOnCommentDeleteListener(OnCommentDeleteListener listener) {
        this.onCommentDeleteListener = listener;
    }
    //*******************************************************************************************


    public CommentAdapterPostulados(ArrayList<ModelComentario> list, Context context, String UserId) {
        this.list = list;
        this.context = context;
        this.userId = UserId;
    }



    //Para representar cada elemento de la lista de comentarios
    @NonNull
    @Override
    public CommentAdapterPostulados.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_comentario,parent,false);
        return new ViewHolder(view);
    }


    //Este método se llama para vincular datos a una instancia de ViewHolder.
    // En este caso, se cargan los datos de un objeto ModelEvento
    // en los elementos de la vista correspondientes (como ImageView, TextView, etc.)
    @Override
    public void onBindViewHolder(@NonNull CommentAdapterPostulados.ViewHolder holder, int position) {


        ModelComentario comentario=list.get(position);

        // Verificar si el comentario tiene respuestas

        ModelRespuestaComentario respuesta = comentario.getRespuesta();



        if (respuesta != null) {

            // Configurar vista para comentario con respuesta
            holder.linearLayoutComentario.setVisibility(View.VISIBLE);
            holder.linearLayoutRespuesta.setVisibility(View.VISIBLE);
            // Deshabilitar el botón "Responder"
            holder.btn_reply.setEnabled(false);
            // Ocultar el botón "Responder"
            holder.btn_reply.setVisibility(View.GONE);

            //**************Comentario Original***************
            //Se carga el itemComentario
            // Cargar la imagen usando Glide desde la URI almacenada en la base de datos
            String imagePathComentario = comentario.getImagen_perfil();
            Glide.with(context)
                    .load(imagePathComentario) // Carga la imagen desde la URI
                    .into(holder.image_profile);

            //Se cargan el resto de campos
            holder.tv_commentComentario.setText(comentario.getComment());
            holder.tv_userNameComentario.setText(comentario.getPublisherName());


            //**********************Respuesta******************
            String imagePathRespuesta = respuesta.getImagen_perfil();
            Glide.with(context)
                    .load(imagePathRespuesta) // Carga la imagen desde la URI
                    .into(holder.image_profileRespuesta);

            //Se cargan el resto de campos
            holder.tv_commentRespuesta.setText(respuesta.getComment());
            holder.tv_userNameRespuesta.setText(respuesta.getPublisherName());


        }else{

            // Configurar vista para comentario sin respuesta
            holder.linearLayoutComentario.setVisibility(View.VISIBLE);
            holder.linearLayoutRespuesta.setVisibility(View.GONE);
            // Para que el botón "Responder" esté habilitado
            holder.btn_reply.setEnabled(true);
            // Para que el botón  botón "Responder" esté visible
            holder.btn_reply.setVisibility(View.VISIBLE);

            //Se carga el itemComentario
            // Cargar la imagen usando Glide desde la URI almacenada en la base de datos
            String imagePath = comentario.getImagen_perfil();
            Glide.with(context)
                    .load(imagePath) // Carga la imagen desde la URI
                    .into(holder.image_profile);

            //Se cargan el resto de campos
            holder.tv_commentComentario.setText(comentario.getComment());
            holder.tv_userNameComentario.setText(comentario.getPublisherName());
        }



        //Agrego comportamiento a los botones

        //Boton Reponder
        // Agregar el listener al botón btn_reply
        holder.btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtiene la posición del adaptador
                int adapterPosition = holder.getAdapterPosition();

                // Comprueba si la posición es válida
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Obtiene los datos del comentario al que se va a responder
                    ModelComentario comentarioAResponder = list.get(adapterPosition);

                    // Crea un Intent para abrir la nueva actividad de respuesta
                    Intent intent = new Intent(context, RespuestaComentarioActivityPostulados.class);
                    Log.d("AdapterContext", "Context: " + context);

                    // Pasa los datos del comentario actual al Intent
                    intent.putExtra("comment", comentarioAResponder.getComment());
                    intent.putExtra("publisherId", comentarioAResponder.getPublisherId());
                    intent.putExtra("publisherName", comentarioAResponder.getPublisherName());
                    intent.putExtra("imagenPerfilUri", comentarioAResponder.getImagen_perfil());
                    intent.putExtra("commentId", comentarioAResponder.getCommentId());
                    intent.putExtra("idEvento", comentarioAResponder.getEventoId());

                    // Inicia la nueva actividad
                    context.startActivity(intent);
                }
            }
        });


        //Boton Eliminar Comenatrio
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Obtiene la posición del adaptador
                int adapterPosition = holder.getAdapterPosition();

                // Comprueba si la posición es válida
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    // Obtiene los datos del comentario al que se va a eliminar
                    ModelComentario comentarioAEliminar = list.get(adapterPosition);

                    if (onCommentDeleteListener != null) {
                        onCommentDeleteListener.onCommentDelete(comentarioAEliminar);
                    }

                }

            }
        });

        //Boton Eliminar Respuesta
        holder.btn_deleteRespuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Obtiene la posición del adaptador
                int adapterPosition = holder.getAdapterPosition();

                // Comprueba si la posición es válida
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    // Obtiene los datos del comentario al que se va a eliminar
                    ModelComentario comentarioAEliminar = list.get(adapterPosition);

                    // Verificar si el comentario tiene respuestas
                    ModelRespuestaComentario respuestaAEliminar = comentarioAEliminar.getRespuesta();

                    if (respuestaAEliminar != null && onResponseDeleteListener != null) {
                        onResponseDeleteListener.onResponseDelete(respuestaAEliminar);



                    }
                }
            }
        });

        if (!Objects.equals(list.get(holder.getAbsoluteAdapterPosition()).getPublisherId(), userId))
        {
            holder.btn_delete.setVisibility(View.GONE);
        }




    }//fin onBindViewHolder





    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_userNameComentario, tv_commentComentario,tv_commentRespuesta,tv_userNameRespuesta;
        ImageView image_profile,image_profileRespuesta;
        Button btn_reply,btn_delete,btn_deleteRespuesta;

        LinearLayout linearLayoutRespuesta,linearLayoutComentario;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Controles del itemComentario para el comentario
            tv_userNameComentario=itemView.findViewById(R.id.tv_userNameComentario);
            tv_commentComentario=itemView.findViewById(R.id.tv_commentComentario);
            image_profile=itemView.findViewById(R.id.image_profile);
            btn_reply=itemView.findViewById(R.id.btn_reply);
            btn_delete=itemView.findViewById(R.id.btn_delete);
            linearLayoutComentario=itemView.findViewById(R.id.linearLayoutComentario);

            //Controles del itemComentario para la respuesta
            image_profileRespuesta=itemView.findViewById(R.id.image_profileRespuesta);
            tv_userNameRespuesta=itemView.findViewById(R.id.tv_userNameRespuesta);
            tv_commentRespuesta=itemView.findViewById(R.id.tv_commentRespuesta);
            btn_deleteRespuesta=itemView.findViewById(R.id.btn_deleteRespuesta);
            linearLayoutRespuesta=itemView.findViewById(R.id.linearLayoutRespuesta);




        }
    }


}
