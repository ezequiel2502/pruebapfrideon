package com.example.sesionconfirebase;


import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.gps_test.BuscarEventosMapaActivity;
import com.example.gps_test.Ruta;
import com.example.gps_test.ui.map.TupleDouble;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.ViewHolderNotificacion>  {

    ArrayList <ModelNotificacion> list;
    String nombreEvento = "";
    String IdPostulante = "";
    String IdEvento = "";
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
    //Acá debería incorporar estos comportamientos basado en los botones de la notificación
    @Override
    public void onBindViewHolder(@NonNull NotificacionesAdapter.ViewHolderNotificacion holder, int position) {
        ModelNotificacion notificacion = list.get(position);
        String mensaje = notificacion.getTitulo() + " " + notificacion.getDetalle();
        holder.tv_MensajeNotificacion.setText(mensaje);
        holder.tv_TipoNotificacion.setText(notificacion.getTipoNotificacion());
        if(!notificacion.getTipoNotificacion().equals("creador_evento"))
        {
            holder.btnAceptar.setVisibility(View.GONE);
            holder.btnCancelar.setVisibility(View.GONE);
        }
        // Configura el clic del botón "Aceptar"
        holder.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 nombreEvento = notificacion.getNombreEvento();
                 IdPostulante = notificacion.getPostulanteId();
                 IdEvento = notificacion.getIdEvento();
                buscarNoAceptadoPorEventoYUsuario(context,IdEvento,IdPostulante);
                DatabaseReference notificacionRef = database.getReference("Notificaciones").child(notificacion.getIdNotificacion());
                // Eliminar la notificación
                notificacionRef.removeValue();
            }
        });
        // Configura el clic del botón "Aceptar"
        holder.btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 nombreEvento = notificacion.getNombreEvento();
                 IdPostulante = notificacion.getPostulanteId();
                 IdEvento = notificacion.getIdEvento();
                notificarDenegacionPostulanteEvento(IdEvento,nombreEvento,IdPostulante);
                notificaDenegacionPostulanteEvento(IdEvento,nombreEvento,IdPostulante, notificacion.getTokenPostulante());
                Toast.makeText(context, "Denegaste la Postulación", Toast.LENGTH_SHORT).show();
                DatabaseReference notificacionRef = database.getReference("Notificaciones").child(notificacion.getIdNotificacion());
                // Eliminar la notificación
                notificacionRef.removeValue();

            }
        });
        holder.btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference notificacionRef = database.getReference("Notificaciones").child(notificacion.getIdNotificacion());
                // Eliminar la notificación
                notificacionRef.removeValue();
                Toast.makeText(context, "Notificacion Borrada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), ListadoNotificacionesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Esto limpia todas las actividades en la parte superior
                v.getContext().startActivity(intent);

            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolderNotificacion extends RecyclerView.ViewHolder {

        //Son los controles del itemEvento
        TextView tv_TipoNotificacion,tv_NombreUsuario,tv_MensajeNotificacion;
        Button btnAceptar, btnCancelar,btnBorrar;
        public ViewHolderNotificacion(@NonNull View itemView) {
            super(itemView);
            tv_TipoNotificacion=itemView.findViewById(R.id.tv_TipoNotificacion);
            tv_MensajeNotificacion=itemView.findViewById(R.id.tv_MensajeNotificacion);
            btnAceptar=itemView.findViewById(R.id.tv_BotonAceptar);
            btnCancelar=itemView.findViewById(R.id.tv_BotonCancelar);
            btnBorrar=itemView.findViewById(R.id.tv_BotonBorrar);
        }
    }
    private void notificarDenegacionPostulanteEvento( String IdEvento,String nombreEvento, String postulanteId) {
        NotificationCounter notificacion = new NotificationCounter();
        notificacion.registrarNotificacionDenegacionPostulanteEvento("Denegaron tu postulacion a : ",nombreEvento,"denegacion_postulante_evento",IdEvento,postulanteId,nombreEvento);
        Intent intent = new Intent(context, ListadoNotificacionesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Esto limpia todas las actividades en la parte superior
        context.startActivity(intent);
    }
    private void notificaDenegacionPostulanteEvento( String IdEvento,String nombreEvento, String postulanteId,String TokenPostulante) {
        Context context=this.context;
        RequestQueue myrequest = Volley.newRequestQueue(context);
        JSONObject json = new JSONObject();

        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Denegaron tu postulacion a : ");
            notificacion.put("detalle", nombreEvento);
            notificacion.put("tipo", "denegacion_postulante_evento");
            notificacion.put("idEvento", IdEvento);
            notificacion.put("postulanteId", postulanteId);
            notificacion.put("tokenPostulante", TokenPostulante);
            json.put("to", TokenPostulante);
            json.put("data", notificacion); // Cambio de "data" a "notification"


            // URL que se utilizará para enviar la solicitud POST al servidor de FCM
            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, null, null) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header;
                    header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "Bearer AAAA2KZHDiM:APA91bHxMVQ1jcd7sRVOqoP9ffdSEFiBnVr_iFKOL0kd_X71Arrc3lSi8is74MYUB6Iyg_1DmbvJK42Ejk-6N-i9g-yDeVjncE09U8GUOVx9YpDWjpDywU_wLXQvCO0ZERz5qZc9_zqM");
                    return header;
                }
            };
            myrequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void buscarNoAceptadoPorEventoYUsuario(Context context,String idEvento, String userId) {
        DatabaseReference prePostulacionesRef = FirebaseDatabase.getInstance().getReference().child("Pre-Postulaciones");

        DatabaseReference eventoRef = prePostulacionesRef.child(idEvento);
        DatabaseReference usuarioRef = eventoRef.child(userId);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PrePostulacion prePostulacion = dataSnapshot.getValue(PrePostulacion.class);

                if (prePostulacion != null && !prePostulacion.getAceptado()) {
                    String tokenFcmPostulante = prePostulacion.getTokenFcmPostulante();

                    usuarioRef.child("aceptado").setValue(true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        //si salio bien lo postula
                                        postularCandidato2(context,idEvento, userId, tokenFcmPostulante);
                                    } else {
                                        // Manejar el error en la actualización
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });
    }
    private void postularCandidato2(Context context, String idEventoRecuperado, String userId, String tokenFcmPostulante) {
        DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(idEventoRecuperado);

        eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);

                    int cupoMaximo = Integer.parseInt(evento.getCupoMaximo());
                    if (cupoMaximo > 0) {
                        int nuevoCupoMaximo = cupoMaximo - 1;
                        evento.setCupoMaximo(String.valueOf(nuevoCupoMaximo));

                        DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");
                        postulacionesRef.child(userId).child(idEventoRecuperado).setValue(true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            evento.agregarParticipante(userId);
                                            eventosRef.setValue(evento)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                if (0 >= nuevoCupoMaximo) {
                                                                    // Notificar al creador del evento
                                                                    notificarCupoMaximoAlcanzado(evento.getNombreEvento(),evento.getIdEvento());
                                                                    notificaCupoMaximoAlcanzado(evento.getIdEvento(),evento.getNombreEvento(),evento.getTokenFCM());
                                                                    Toast.makeText(context, "Se alcanzó el cupo máximo", Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    notificarPostulanteEvento(IdEvento,nombreEvento,IdPostulante);
                                                                    notificaPostulanteEvento(IdEvento,nombreEvento,tokenFcmPostulante);
                                                                    Toast.makeText(context, "Aceptaste postulación", Toast.LENGTH_SHORT).show();
                                                                }


                                                            } else {
                                                                // Error en la modificación del evento
                                                                Toast.makeText(context, "Error al modificar el evento", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // Error al agregar la información de postulación
                                            Toast.makeText(context, "Error al postularte al evento", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        // No hay cupo disponible
                        notificarCupoMaximoAlcanzado(evento.getNombreEvento(),evento.getIdEvento());
                        notificaCupoMaximoAlcanzado(evento.getIdEvento(),evento.getNombreEvento(),evento.getTokenFCM());
                        Toast.makeText(context, "Se alcanzó el cupo máximo", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });
    }

    private void notificarCupoMaximoAlcanzado(String nombreEvento,String idEvento){


        NotificationCounter notificacion = new NotificationCounter();
// Crea una referencia al evento que quieres recuperar
        DatabaseReference eventoRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(idEvento);

// Agrega un listener para escuchar los cambios en el evento
        eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Se ejecuta cuando los datos del evento han cambiado
                if (dataSnapshot.exists()) {
                    // El evento existe en la base de datos
                    // Ahora puedes obtener el idOrganizador
                    String idOrganizador = dataSnapshot.child("userId").getValue(String.class);
                    notificacion.registrarNotificacionCupoMaximoAlcanzado("Cupo Máximo Alcanzado en:",nombreEvento,"cupo-maximo",idEvento,idOrganizador,nombreEvento);
                    Intent intent = new Intent(context, ListadoNotificacionesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                } else {
                    // El evento no existe en la base de datos
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Se ejecuta si hay un error en la operación
                System.out.println("Error al recuperar el evento: " + databaseError.getMessage());
            }
        });
    }
    public void notificaCupoMaximoAlcanzado(String idEvento,String nombreEvento, String TokenFCM)
    {
        Context mContext = null;
        mContext=this.context;
        RequestQueue myrequest = Volley.newRequestQueue(mContext);
        JSONObject json = new JSONObject();

        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Cupo Máximo Alcanzado en: ");
            notificacion.put("detalle", nombreEvento);
            notificacion.put("tipo", "cupo-maximo");
            notificacion.put("idEvento", idEvento);
            notificacion.put("tokenCreador", TokenFCM);
            json.put("to", TokenFCM);
            json.put("data", notificacion); // Cambio de "data" a "notification"


            // URL que se utilizará para enviar la solicitud POST al servidor de FCM
            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, null, null) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header;
                    header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "Bearer AAAA2KZHDiM:APA91bHxMVQ1jcd7sRVOqoP9ffdSEFiBnVr_iFKOL0kd_X71Arrc3lSi8is74MYUB6Iyg_1DmbvJK42Ejk-6N-i9g-yDeVjncE09U8GUOVx9YpDWjpDywU_wLXQvCO0ZERz5qZc9_zqM");
                    return header;
                }
            };
            myrequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void notificarPostulanteEvento( String IdEvento,String nombreEvento,String postulanteId) {
        NotificationCounter notificacion = new NotificationCounter();
        notificacion.registrarNotificacionPostulanteEvento("Aceptaron tu postulacion a :",nombreEvento,"postulante_evento",IdEvento,postulanteId,nombreEvento);
        Toast.makeText(context, "Aceptaste la Postulación", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, ListadoNotificacionesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
    private void notificaPostulanteEvento( String IdEvento,String postulanteId,String TokenPostulante) {
        Context context=this.context;
        RequestQueue myrequest = Volley.newRequestQueue(context);
        JSONObject json = new JSONObject();

        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Aceptaron tu postulacion a : ");
            notificacion.put("detalle", nombreEvento);
            notificacion.put("tipo", "postulante_evento");
            notificacion.put("idEvento", IdEvento);
            notificacion.put("postulanteId", postulanteId);
            notificacion.put("tokenPostulante", TokenPostulante);
            json.put("to", TokenPostulante);
            json.put("data", notificacion); // Cambio de "data" a "notification"


            // URL que se utilizará para enviar la solicitud POST al servidor de FCM
            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, null, null) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header;
                    header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "Bearer AAAA2KZHDiM:APA91bHxMVQ1jcd7sRVOqoP9ffdSEFiBnVr_iFKOL0kd_X71Arrc3lSi8is74MYUB6Iyg_1DmbvJK42Ejk-6N-i9g-yDeVjncE09U8GUOVx9YpDWjpDywU_wLXQvCO0ZERz5qZc9_zqM");
                    return header;
                }
            };
            myrequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
