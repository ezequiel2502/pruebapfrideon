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

public class SingleEventoPublicoActivity extends AppCompatActivity {
    TextView tv_SingleEvento,tv_SingleRuta,tv_SingleDescripcion,tv_SingleFechaEncuentro,
            tv_SingleHoraEncuentro,tv_SingleCupoMinimo,tv_SingleCupoMaximo,
            tv_SingleCategoria,tv_SingleUserName,tv_SingleUserId,
            tv_SinglePublicoPrivado,tv_SingleActivadoDescativado;
    ImageView singleImage;

    RatingBar rb_SingleRatingEvento;

    Button btn_postularse;

    ImageView image_profile;

    private static String tokenFcmPostulante;


    EditText txt_write_comment;
    TextView tv_add_comment;

   RecyclerView recyclerViewComments;
    ArrayList<ModelComentario> recycleList;

    private DatabaseReference commentsRef;
    private ValueEventListener commentsListener;

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
        Integer singleRating=getIntent().getIntExtra("singleRating",0);
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
        CommentAdapter commentAdapter=new CommentAdapter(recycleList,SingleEventoPublicoActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewComments.setLayoutManager(linearLayoutManager);
        recyclerViewComments.addItemDecoration(new DividerItemDecoration(recyclerViewComments.getContext(),DividerItemDecoration.VERTICAL));
        recyclerViewComments.setNestedScrollingEnabled(false);
        recyclerViewComments.setAdapter(commentAdapter);



            //reviso cambios en el nodo Comentarios
        commentsRef = FirebaseDatabase.getInstance().getReference("Comentarios").child(singleIdEvento);
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Recorre todos los hijos bajo el nodo del evento
                for (DataSnapshot comentarioSnapshot : dataSnapshot.getChildren()) {
                    // Obtiene los valores de los campos "comment" y "publisher"
                    String comment = comentarioSnapshot.child("comment").getValue(String.class);
                    String publisherId = comentarioSnapshot.child("publisherId").getValue(String.class);
                    String publisherName = comentarioSnapshot.child("publisherName").getValue(String.class);
                    String imagen_perfil = comentarioSnapshot.child("imagenPerfilUri").getValue(String.class);
                    String commentId = comentarioSnapshot.child("commentId").getValue(String.class);
                    String idEvento = comentarioSnapshot.child("idEvento").getValue(String.class);
//



                   // Creo y agrega el objeto a tu lista o adaptador (`recycleList`)
                    ModelComentario modelComentario = new ModelComentario(publisherId, comment, imagen_perfil,publisherName,commentId,idEvento);

                    // Agrega el objeto a tu lista o adaptador (en tu caso, `recycleList`)
                    recycleList.add(modelComentario);
                }

                // Notifica al adaptador que los datos han cambiado
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






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




    }//fin onCreate()


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
                hashMap.put("imagenPerfilUri", imagenPerfilUri); // Agrego la URI de la imagen de perfil



                // Utilizo el identificador único para almacenar el comentario
                reference.child(comentarioId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SingleEventoPublicoActivity.this, "Se publicó el mensaje", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SingleEventoPublicoActivity.this, "No se publicó el mensaje", Toast.LENGTH_SHORT).show();
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

}//Fin Appp
