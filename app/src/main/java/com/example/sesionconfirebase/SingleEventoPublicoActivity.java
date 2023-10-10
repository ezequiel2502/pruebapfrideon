package com.example.sesionconfirebase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleEventoPublicoActivity extends AppCompatActivity implements CommentAdapter.OnResponseDeleteListener, CommentAdapter.OnCommentDeleteListener {
    TextView tv_SingleEvento,tv_SingleRuta,tv_SingleDescripcion,tv_SingleFechaEncuentro,
            tv_SingleHoraEncuentro,tv_SingleCupoMinimo,tv_SingleCupoMaximo,
            tv_SingleCategoria,tv_SingleUserName,tv_SingleUserId,
            tv_SinglePublicoPrivado,tv_SingleActivadoDescativado;
    ImageView singleImage;

    RatingBar rb_SingleRatingEvento;

    Button btn_postularse,btn_CancelarEvento;


    ImageView image_profile;

    private static String tokenFcmPostulante;


    EditText txt_write_comment;
    TextView tv_add_comment;

   RecyclerView recyclerViewComments;

    ArrayList<ModelComentario> recycleList;

   private CommentAdapter commentAdapter;

    private DatabaseReference commentsRef;
    private ValueEventListener commentsListener;

    private  ModelEvento modelEventoActual;

    @Override
    protected void onRestart() {
        super.onRestart();


        cargarComentarios();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_evento_publico_vigente);

        // Obtiene el token de registro de FCM para el postulante
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tokenFcmPostulante = task.getResult();

            }else {
                tokenFcmPostulante="sin token";
            }
        });


        tv_SingleEvento=findViewById(R.id.tv_SingleEvento);
        tv_SingleRuta=findViewById(R.id.tv_SingleRuta);
        tv_SingleDescripcion=findViewById(R.id.tv_SingleDescripcion);
        tv_SingleFechaEncuentro=findViewById(R.id.tv_SingleFechaEncuentro);
        tv_SingleHoraEncuentro=findViewById(R.id.tv_SingleHoraEncuentro);
        tv_SingleCupoMinimo=findViewById(R.id.tv_SingleCupoMinimo);
        tv_SingleCupoMaximo=findViewById(R.id.tv_SingleCupoMaximo);
        tv_SingleCategoria=findViewById(R.id.tv_SingleCategoria);
        tv_SingleUserName=findViewById(R.id.tv_SingleUserName);
        tv_SingleUserId=findViewById(R.id.tv_SingleUserId);
        tv_SinglePublicoPrivado=findViewById(R.id.tv_SinglePublicoPrivado);
        tv_SingleActivadoDescativado=findViewById(R.id.tv_SingleActivadoDescativado);
        rb_SingleRatingEvento=findViewById(R.id.rb_SingleRatingEvento);
        btn_postularse=findViewById(R.id.btn_postularse);
        btn_CancelarEvento=findViewById(R.id.btn_CancelarEvento);



        //Obtengo los datos de los intents

        singleImage=findViewById(R.id.singleImage);

        String imageUrl = getIntent().getStringExtra("singleImage"); // Obtén la URL de la imagen del Intent
        Glide.with(this)
                .load(imageUrl)
                .into(singleImage); // Carga la imagen en el ImageView del SingleActivity


        String singleEvento = getIntent().getStringExtra("singleEvento");
        String singleRuta = getIntent().getStringExtra("singleRuta");
        String singleDescripcion = getIntent().getStringExtra("singleDescripcion");
        String singleFechaEncuentro = getIntent().getStringExtra("singleFechaEncuentro");
        String singleHoraEncuentro = getIntent().getStringExtra("singleHoraEncuentro");
        String singleCupoMinimo = getIntent().getStringExtra("singleCupoMinimo");
        String singleCupoMaximo = getIntent().getStringExtra("singleCupoMaximo");
        String singleCategoria = getIntent().getStringExtra("singleCategoria");
        String singleUserName=getIntent().getStringExtra("singleUserName");
        String singleUserId=getIntent().getStringExtra("singleUserId");
        String singleActivarDesactivar=getIntent().getStringExtra("singleActivarDesactivar");
        String singlePublicoPrivado=getIntent().getStringExtra("singlePublicoPrivado");
        Float singleRating=  getIntent().getFloatExtra("singleRating",0);
        String singleIdEvento=getIntent().getStringExtra("EventoId");
        String singleTokenFCM=getIntent().getStringExtra("TokenFCM");//del creador del evento


        // Establece los datos en los TextViews
        tv_SingleEvento.setText(singleEvento);
        tv_SingleRuta.setText(singleRuta);
        tv_SingleDescripcion.setText(singleDescripcion);
        tv_SingleFechaEncuentro.setText(singleFechaEncuentro);
        tv_SingleHoraEncuentro.setText(singleHoraEncuentro);
        tv_SingleCupoMinimo.setText(singleCupoMinimo);
        tv_SingleCupoMaximo.setText(singleCupoMaximo);
        tv_SingleCategoria.setText(singleCategoria);
        tv_SingleUserName.setText(singleUserName);
        tv_SingleUserId.setText(singleUserId);
        rb_SingleRatingEvento.setRating(singleRating);
        tv_SinglePublicoPrivado.setText(singlePublicoPrivado);
        tv_SingleActivadoDescativado.setText(singleActivarDesactivar);


        //Guardo en sharedPreferences algunos datos utiles para las notificaciones
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);

        // Para guardar el idEvento en SharedPreferences
        String idEvento = singleIdEvento;
        String tokenFcm=singleTokenFCM;
        String nombreEvento=singleEvento;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idEvento", idEvento);
        editor.putString("TokenFCM", tokenFcm);//del creador del evento
        editor.putString("tokenFcmPostulante", tokenFcmPostulante);
        editor.putString("nombreEvento", nombreEvento);
        editor.apply();


        //Controles para los comentarios
        txt_write_comment=findViewById(R.id.txt_write_comment);
        tv_add_comment=findViewById(R.id.tv_add_comment);
        image_profile=findViewById(R.id.image_profile);

        //recyclerViewComments
        recyclerViewComments=findViewById(R.id.recyclerViewComments);
        recycleList=new ArrayList<>();

        //Creo una instancia del adapter para los comentarios
        commentAdapter = new CommentAdapter(recycleList, SingleEventoPublicoActivity.this);
        //Seteo los listener de las interfaces de comunicacion para el adapter
        commentAdapter.setOnCommentDeleteListener(this);
        commentAdapter.setOnResponseDeleteListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewComments.setLayoutManager(linearLayoutManager);
        recyclerViewComments.addItemDecoration(new DividerItemDecoration(recyclerViewComments.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewComments.setNestedScrollingEnabled(false);
        recyclerViewComments.setAdapter(commentAdapter);


        //Revisa el nodo "comentarios" y actualiza la lista de los mismos
        cargarComentarios();




        //*************Para activar/desactivar el btn_CancelarEvento

        // Primero obtén una instancia de la base de datos
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        // Luego, obtén una referencia a la ubicación específica de la base de datos que necesitas
        DatabaseReference eventoRef = firebaseDatabase.getReference().child("Eventos").child("Eventos Publicos").child(singleIdEvento);

        eventoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    //Obtengo el objeto evento
                    ModelEvento modelEvento = dataSnapshot.getValue(ModelEvento.class);

                    //Usuario logueado
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String currentUserId=currentUser.getUid();

                    // Compara los IDs de usuario
                    if (modelEvento.getUserId().equals(currentUserId)) {
                        // Los IDs coinciden, habilita el botón, porque se trata del creador de ese evento
                        btn_CancelarEvento.setEnabled(true);
                        btn_CancelarEvento.setVisibility(View.VISIBLE);
                    } else {
                        // Los IDs no coinciden, deshabilita el botón
                        btn_CancelarEvento.setEnabled(false);
                        btn_CancelarEvento.setVisibility(View.INVISIBLE);
                    }

                    // Guarda el ModelEvento en un miembro de clase, para usarlo luego ese objeto
                          modelEventoActual = modelEvento;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });

        //**********************************************************



        //Para agregar un comentario
        tv_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txt_write_comment.getText().toString().equals("")){
                    Toast.makeText(SingleEventoPublicoActivity.this, "No se pueden enviar mensajes vacios!!!", Toast.LENGTH_SHORT).show();
                }else {
                    addComment(singleIdEvento);
                }
            }
        });


        //Referencia al nodo postulaciones que uso dentro del boton Postularse
        DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");



        //Boton Postularse
        btn_postularse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Envia notificacion al creador del evento
                NotificarCreadorEvento();
                prePostularCandidato2();


            }
        });


        btn_CancelarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });




    }//fin onCreate()

    private void cargarComentarios() {

        // Obtener el id del evento
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);
        String idEvento = sharedPreferences.getString("idEvento", "");



        //Reviso el nodo comentarios
        commentsRef = FirebaseDatabase.getInstance().getReference("Comentarios").child(idEvento);
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar la lista actual antes de cargar los nuevos comentarios
                recycleList.clear();


                // Recorre todos los hijos bajo el nodo del evento
                for (DataSnapshot comentarioSnapshot : dataSnapshot.getChildren()) {
                    String comment = comentarioSnapshot.child("comment").getValue(String.class);
                    String publisherId = comentarioSnapshot.child("publisherId").getValue(String.class);
                    String publisherName = comentarioSnapshot.child("publisherName").getValue(String.class);
                    String imagen_perfil = comentarioSnapshot.child("imagenPerfilUri").getValue(String.class);
                    String commentId = comentarioSnapshot.child("commentId").getValue(String.class);
                    String idEvento = comentarioSnapshot.child("idEvento").getValue(String.class);
                    String tipo = comentarioSnapshot.child("tipo").getValue(String.class);
                    String parentCommentId = comentarioSnapshot.child("parentCommentId").getValue(String.class);

                    if ("comentario".equals(tipo)) {
                        // Es un comentario principal, crear un objeto ModelComentario
                        ModelComentario modelComentario = new ModelComentario(publisherId, comment, imagen_perfil, publisherName, commentId, idEvento, tipo);

                        // Agregar el comentario a tu lista o adaptador (en tu caso, `recycleList`)
                        recycleList.add(modelComentario);
                    } else {
                        // Es una respuesta, buscar el comentario al que responde

                        for (ModelComentario comentario : recycleList) {
                            if (comentario.getCommentId().equals(parentCommentId)) {
                                // Crear un objeto ModelRespuestaComentario
                                ModelRespuestaComentario modelRespuesta = new ModelRespuestaComentario(publisherId, comment, imagen_perfil, publisherName, commentId, idEvento, parentCommentId, tipo);

                                // Agregar la respuesta al comentario
                                comentario.setRespuesta(modelRespuesta);
                                break;
                            }
                        }
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar el error si es necesario
            }
        });
    }



    private void postularCandidato(){
        // Para recuperar el idEvento almacenado en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);
        String idEventoRecuperado = sharedPreferences.getString("idEvento", "");

        // Buscar el evento
        DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(idEventoRecuperado);

        eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);

                    // Verificar si hay cupo disponible
                    int cupoMaximo = Integer.parseInt(evento.getCupoMaximo());
                    if (cupoMaximo > 0) {
                        // Modificar el valor del cupoMaximo
                        int nuevoCupoMaximo = cupoMaximo - 1;
                        evento.setCupoMaximo(String.valueOf(nuevoCupoMaximo));

                        // Obtener el ID del usuario actual
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        // Agregar la información de postulación al nodo "postulaciones"
                        DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");
                        postulacionesRef.child(userId).child(idEventoRecuperado).setValue(true)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Actualizar el evento modificado en la base de datos
                                            eventosRef.setValue(evento)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Modificación exitosa
                                                                Toast.makeText(getApplicationContext(), "Te has postulado al evento", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                // Error en la modificación del evento
                                                                Toast.makeText(getApplicationContext(), "Error al modificar el evento", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // Error al agregar la información de postulación
                                            Toast.makeText(getApplicationContext(), "Error al postularte al evento", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        // No hay cupo disponible
                        Toast.makeText(getApplicationContext(), "Se alcanzó el cupo máximo", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });
    }

    private void postularCandidato2(String idEventoRecuperado, String userId, String tokenFcmPostulante) {
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
                                            eventosRef.setValue(evento)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Realizar la postulación exitosa
                                                                Toast.makeText(getApplicationContext(), "Te has postulado al evento", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                // Error en la modificación del evento
                                                                Toast.makeText(getApplicationContext(), "Error al modificar el evento", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            // Error al agregar la información de postulación
                                            Toast.makeText(getApplicationContext(), "Error al postularte al evento", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        // No hay cupo disponible
                        Toast.makeText(getApplicationContext(), "Se alcanzó el cupo máximo", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error de cancelación
            }
        });
    }
    private void prePostularCandidato2() {
        // Para recuperar el idEvento almacenado en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);
        String idEventoRecuperado = sharedPreferences.getString("idEvento", "");



        // Buscar el evento
        DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(idEventoRecuperado);

        eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);

                    // Obtener el ID del usuario actual o postulante
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


                    // Agregar la información de pre-postulación al nodo "Pre-Postulaciones"

                    DatabaseReference prePostulacionesRef = FirebaseDatabase.getInstance().getReference().child("Pre-Postulaciones");
                    prePostulacionesRef.child(idEventoRecuperado).child(userId).setValue(new PrePostulacion(tokenFcmPostulante,false))
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Pre-postulación exitosa
                                        Toast.makeText(getApplicationContext(), "Te has pre-postulado al evento", Toast.LENGTH_SHORT).show();

                                    } else {
                                        // Error en la pre-postulación
                                        Toast.makeText(getApplicationContext(), "Error al pre-postularte al evento", Toast.LENGTH_SHORT).show();
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


    private void prePostularCandidato() {
        // Para recuperar el idEvento almacenado en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);
        String idEventoRecuperado = sharedPreferences.getString("idEvento", "");

        // Buscar el evento
        DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(idEventoRecuperado);

        eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);

                    // Obtener el ID del usuario actual o postulante
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // Agregar la información de postulación al nodo "pre-postulaciones"
                    DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Pre-Postulaciones");
                    postulacionesRef.child(idEventoRecuperado).child(userId).child(tokenFcmPostulante).child("aceptado").setValue(false)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Modificación exitosa
                                        Toast.makeText(getApplicationContext(), "Te has pre-postulado al evento", Toast.LENGTH_SHORT).show();

                                    } else {
                                        // Error en la modificación del evento
                                        Toast.makeText(getApplicationContext(), "Error al modificar el evento", Toast.LENGTH_SHORT).show();
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




    private void guardarTokenPostulados() {
        // Obtener el ID del usuario actual, el que se va a postular
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtener el token de FCM(Firebase Cloud Messaging)
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();

                // Crear el HashMap con los datos de postulación
                HashMap<String, Object> postulacionMap = new HashMap<>();
                postulacionMap.put("token", token);
                postulacionMap.put("confirmado", false);

                // Guardar la información en la base de datos
                DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");
                postulacionesRef.child(userId).setValue(postulacionMap)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                // Postulación exitosa
                                Toast.makeText(getApplicationContext(), "Postulación enviada. Espera confirmación.", Toast.LENGTH_LONG).show();
                            } else {
                                // Error en la postulación
                                Toast.makeText(getApplicationContext(), "Error en la postulación. Inténtalo nuevamente.", Toast.LENGTH_LONG).show();
                            }
                        });

            } else {
                // La obtención del token de FCM falló
                Exception exception = task.getException();
                if (exception != null) {
                    Toast.makeText(getApplicationContext(), "No se pudo obtner token", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    private void consultarToken() {
        // Obtener el token guardado
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("token").child("leo");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult().getValue(String.class);
                if (token != null && !token.isEmpty()) {
                    //enviarNotificacion(token);
                } else {
                    Log.e("Tag", "No se encontró un token válido.");
                }
            } else {
                Log.e("Tag", "Error al obtener el token.");
            }
        });
    }//fin obtnerToken()

    private void NotificarCreadorEvento() {

        //"Bearer AAAA2KZHDiM:APA91bHxMVQ1jcd7sRVOqoP9ffdSEFiBnVr_iFKOL0kd_X71Arrc3lSi8is74MYUB6Iyg_1DmbvJK42Ejk-6N-i9g-yDeVjncE09U8GUOVx9YpDWjpDywU_wLXQvCO0ZERz5qZc9_zqM"

        //userName del que se postula
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        //userId del que se postula
        String postulanteId=FirebaseAuth.getInstance().getUid();

        // Para recuperar el tokenFcm almacenado en SharedPreferences del creador de
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);
        String idEvento = sharedPreferences.getString("idEvento", "");
        String tokenFcmRecuperado = sharedPreferences.getString("TokenFCM", "");
        String tokenFcmPostulante= sharedPreferences.getString("tokenFcmPostulante", "");
        String nombreEvento = sharedPreferences.getString("nombreEvento", "");




        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();

        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Aceptar Postulacion de: ");
            notificacion.put("detalle", userName);
            notificacion.put("tipo", "creador_evento");
            notificacion.put("idEvento", idEvento);
            notificacion.put("postulanteId", postulanteId);
            notificacion.put("tokenCreador", tokenFcmRecuperado);
            notificacion.put("tokenPostulante", tokenFcmPostulante);
            notificacion.put("nombreEvento", nombreEvento);

            json.put("to", tokenFcmRecuperado);
            json.put("data", notificacion); // Cambio de "data" a "notification"


            // URL que se utilizará para enviar la solicitud POST al servidor de FCM
            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, null, null) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
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


    private void addComment(String idEvento) {
        // Obtengo el usuario actual que va a publicar el mensaje
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = user.getUid();
        String userName = user.getDisplayName();

        // Obtengo la URI de la imagen de perfil desde Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Default-Profile/doomer.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // La URI de la imagen de perfil se ha obtenido exitosamente
                String imagenPerfilUri = uri.toString();

                // Creo el nodo y los subnodos para agregar el comentario con un identificador único
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comentarios").child(idEvento);
                String comentarioId = reference.push().getKey(); // Genera un ID único para el comentario
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("comment", txt_write_comment.getText().toString());
                hashMap.put("publisherId", userId);
                hashMap.put("publisherName", userName);
                hashMap.put("commentId", comentarioId);
                hashMap.put("idEvento", idEvento);
                hashMap.put("tipo", "comentario");
                hashMap.put("imagenPerfilUri", imagenPerfilUri); // Agrego la URI de la imagen de perfil



                // Utilizo el identificador único para almacenar el comentario
                reference.child(comentarioId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SingleEventoPublicoActivity.this, "Se publicó el mensaje", Toast.LENGTH_SHORT).show();

                                // Llamar a cargarComentarios después de agregar el comentario
                                cargarComentarios();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SingleEventoPublicoActivity.this, "No se publicó el mensaje", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Maneja el error si no se puede obtener la URI de la imagen de perfil
                Toast.makeText(SingleEventoPublicoActivity.this, "No se pudo obtener la imagen de perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }




    //Terminar esto cuando este el perfil del usuario en la Base de datos
    private void getProfileImage(){

    }


    @Override
    public void onResponseDelete(ModelRespuestaComentario respuestaAEliminar) {
        //borro solo la respuesta

        //***************Respuesta*****************
        String respuestaId=respuestaAEliminar.getCommentId();

        // Eliminar el comentario de la base de datos
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comentarios").child(respuestaAEliminar.getEventoId());
        reference.child(respuestaId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Operación exitosa
                Toast.makeText(SingleEventoPublicoActivity.this, "Respuesta eliminada", Toast.LENGTH_SHORT).show();

                cargarComentarios();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el error
                Toast.makeText(SingleEventoPublicoActivity.this, "Error al eliminar respuesta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCommentDelete(ModelComentario comentarioAEliminar) {
        // Obtener el ID del comentario a eliminar
        String commentId = comentarioAEliminar.getCommentId();

        // Verificar si el comentario tiene respuestas
        ModelRespuestaComentario respuestaAEliminar = comentarioAEliminar.getRespuesta();



        if (respuestaAEliminar != null) {
            // Borro respuesta y comentario

            //***************Respuesta*****************
            String respuestaId = respuestaAEliminar.getCommentId();

            // Eliminar el comentario de la base de datos
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comentarios").child(respuestaAEliminar.getEventoId());
            reference.child(respuestaId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    //***************Comentario*****************
                    // Obtener el ID del comentario
                    String commentId = comentarioAEliminar.getCommentId();

                    // Eliminar el comentario de la base de datos
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comentarios").child(comentarioAEliminar.getEventoId());
                    reference.child(commentId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Notificar al usuario que el comentario se eliminó con éxito
                            Toast.makeText(SingleEventoPublicoActivity.this, "Comentario y respuesta eliminados", Toast.LENGTH_SHORT).show();
                            cargarComentarios();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar el error si no se puede eliminar el comentario
                            Toast.makeText(SingleEventoPublicoActivity.this, "Error al eliminar el comentario", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Manejar el error si no se puede eliminar la respuesta
                    Toast.makeText(SingleEventoPublicoActivity.this, "Error al eliminar la respuesta", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            //Borro comentario


            // Eliminar el comentario de la base de datos
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comentarios").child(comentarioAEliminar.getEventoId());
            reference.child(comentarioAEliminar.getCommentId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Notificar al usuario que el comentario se eliminó con éxito
                    Toast.makeText(SingleEventoPublicoActivity.this, "Comentario eliminado", Toast.LENGTH_SHORT).show();
                    cargarComentarios();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)  {
                    // Manejar el error si no se puede eliminar el comentario
                    Toast.makeText(SingleEventoPublicoActivity.this, "Error al eliminar el comentario", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}//Fin Appp
