package com.example.sesionconfirebase;


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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
                                                                    Toast.makeText(context, "Se alcanzó el cupo máximo", Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    notificarPostulanteEvento(IdEvento,nombreEvento,IdPostulante);
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

//        SharedPreferences sharedPreferences = mContext.getSharedPreferences("SPNotificationActionReceiver", Context.MODE_PRIVATE);
//
//        String idEvento = sharedPreferences.getString("idEvento", "");
//        String nombreEvento = sharedPreferences.getString("nombreEvento", "");
//        String tokenCreador = sharedPreferences.getString("tokenCreador", "");
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
    private void notificarPostulanteEvento( String IdEvento,String nombreEvento,String postulanteId) {
        NotificationCounter notificacion = new NotificationCounter();
        notificacion.registrarNotificacionPostulanteEvento("Aceptaron tu postulacion a :",nombreEvento,"postulante_evento",IdEvento,postulanteId,nombreEvento);
        Toast.makeText(context, "Aceptaste la Postulación", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, ListadoNotificacionesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
