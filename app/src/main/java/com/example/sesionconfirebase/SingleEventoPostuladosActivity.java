package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.ListaEventoCompletados.firebaseDatabase;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.gps_test.BuscarEventosMapaActivity;
import com.example.gps_test.PlanificarRuta;
import com.example.gps_test.Ruta;
import com.example.gps_test.ui.map.TupleDouble;
import com.example.sesionconfirebase.ActivityRutasRecyclerView.MyListAdapterActivity;
import com.example.sesionconfirebase.ActivityRutasRecyclerView.MyListDataActivity;
import com.example.sesionconfirebase.ActivitySingleEventoPostuladosRecycler.AssistanceAdapter;
import com.example.sesionconfirebase.ActivitySingleEventoPostuladosRecycler.AssistanceData;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SingleEventoPostuladosActivity extends AppCompatActivity implements CommentAdapterPostulados.OnResponseDeleteListener, CommentAdapterPostulados.OnCommentDeleteListener {


    TextView tv_SingleEvento, tv_SingleRuta, tv_SingleDescripcion, tv_SingleFechaEncuentro,
            tv_SingleHoraEncuentro, tv_SingleCupoMinimo, tv_SingleCupoMaximo,
            tv_SingleCategoria, tv_SingleUserName, tv_SingleUserId,
            tv_SinglePublicoPrivado, tv_SingleActivadoDescativado;
    ImageView singleImage;

    ImageView image_profile;


    RatingBar rb_SingleRatingEvento;

    Button btn_CancelarPostulacion;

    EditText txt_write_comment;
    TextView tv_add_comment;

    RecyclerView recyclerViewComments;

    ArrayList<ModelComentario> recycleList;

    private CommentAdapterPostulados commentAdapter;

    private DatabaseReference commentsRef;
    private String singleIdEvento;
    private String disableAssistanceButtons = "False";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_evento_postulados);


        tv_SingleEvento = findViewById(R.id.tv_SingleEvento);
        tv_SingleRuta = findViewById(R.id.tv_SingleRuta);
        tv_SingleDescripcion = findViewById(R.id.tv_SingleDescripcion);
        tv_SingleFechaEncuentro = findViewById(R.id.tv_SingleFechaEncuentro);
        tv_SingleHoraEncuentro = findViewById(R.id.tv_SingleHoraEncuentro);
        tv_SingleCupoMinimo = findViewById(R.id.tv_SingleCupoMinimo);
        tv_SingleCupoMaximo = findViewById(R.id.tv_SingleCupoMaximo);
        tv_SingleCategoria = findViewById(R.id.tv_SingleCategoria);
        tv_SingleUserName = findViewById(R.id.tv_SingleUserName);
        tv_SingleUserId = findViewById(R.id.tv_SingleUserId);
        tv_SinglePublicoPrivado = findViewById(R.id.tv_SinglePublicoPrivado);
        tv_SingleActivadoDescativado = findViewById(R.id.tv_SingleActivadoDescativado);
        rb_SingleRatingEvento = findViewById(R.id.rb_SingleRatingEvento);
        btn_CancelarPostulacion = findViewById(R.id.btn_CancelarPostulacion);




        //Obtengo los datos de los intents

        singleImage = findViewById(R.id.singleImage);

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
        String singleUserName = getIntent().getStringExtra("singleUserName");
        String singleUserId = getIntent().getStringExtra("singleUserId");
        String singleActivarDesactivar = getIntent().getStringExtra("singleActivarDesactivar");
        String singlePublicoPrivado = getIntent().getStringExtra("singlePublicoPrivado");
        Integer singleRating = getIntent().getIntExtra("singleRating", 0);
        singleIdEvento = getIntent().getStringExtra("EventoId");

        if(getIntent().getStringExtra("isReportInstance") != null)
        {
            TextView tv_etiquetaRating = findViewById(R.id.tv_etiquetaRatingBar);
            tv_etiquetaRating.setVisibility(View.GONE);
            rb_SingleRatingEvento.setVisibility(View.GONE);
            btn_CancelarPostulacion.setVisibility(View.GONE);
            LinearLayout tv_comments = findViewById(R.id.containerComments);
            tv_comments.setVisibility(View.GONE);
            disableAssistanceButtons = "True";
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refRoutes = database.getReference().child("Route");

        ActivityResultLauncher<Intent> callLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            ListaEventoCompletados.actualizarEventoCompletado();
                            Intent data = result.getData();
                            if (data.getStringExtra("Result").equals("Calificar"))
                            {
                                String eventoId = data.getStringExtra("EventID");
                                DatabaseReference completadosRef = firebaseDatabase.getReference().child("Eventos").child("Completados").child(eventoId);
                                completadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            ModelEvento evento = snapshot.getValue(ModelEvento.class);
                                            Intent intent = new Intent(SingleEventoPostuladosActivity.this, CalificarActivity.class);
                                            intent.putExtra("calificacion_gral", String.valueOf(evento.getCalificacionGeneral()));
                                            intent.putExtra("EventoId", evento.getIdEvento());
                                            intent.putExtra("OrganizadorId", evento.getUserId());
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(SingleEventoPostuladosActivity.this,"Por favor espere", Toast.LENGTH_LONG).show();
                                            new Timer().schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    completadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (snapshot.exists()) {
                                                                ModelEvento evento = snapshot.getValue(ModelEvento.class);
                                                                Intent intent = new Intent(SingleEventoPostuladosActivity.this, CalificarActivity.class);
                                                                intent.putExtra("calificacion_gral", String.valueOf(evento.getCalificacionGeneral()));
                                                                intent.putExtra("EventoId", evento.getIdEvento());
                                                                intent.putExtra("OrganizadorId", evento.getUserId());
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                            else
                                                            {
                                                                Toast.makeText(SingleEventoPostuladosActivity.this,"Lo sentimos, los datos aún se están actualizando, intente en unos momentos", Toast.LENGTH_LONG).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
//                                                    runOnUiThread(new Runnable(){
//                                                        @Override
//                                                        public void run(){
//                                                            adapter.notifyDataSetChanged();
//                                                        }
//                                                    });
                                                }
                                            }, 1000);

//                                            DatabaseReference completadosRef = firebaseDatabase.getReference().child("Eventos").child("Eventos Publicos").child(eventoId);
//                                            completadosRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                    ModelEvento evento = snapshot.getValue(ModelEvento.class);
//                                                    Intent intent = new Intent(SingleEventoPostuladosActivity.this, CalificarActivity.class);
//                                                    intent.putExtra("calificacion_gral", String.valueOf(evento.getCalificacionGeneral()));
//                                                    intent.putExtra("EventoId", evento.getIdEvento());
//                                                    intent.putExtra("OrganizadorId", evento.getUserId());
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                }
//                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            else
                            {
                                ListaEventoCompletados.actualizarEventoCompletado();
                                Intent intent = new Intent(SingleEventoPostuladosActivity.this, HomeActivity.class);
                                startActivity(intent);
                                //finish();
                            }
                        }
                    }
                });
        singleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    double d = Double.parseDouble(singleRuta);
                    refRoutes.child(singleRuta).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Ruta ruta_actual = snapshot.getValue(Ruta.class);
                            List<TupleDouble> camino = ruta_actual.routePoints;
                            List<com.example.gps_test.ui.recyclerView.MyListData> resumen = ruta_actual.routeResumeData;

                            Toast.makeText(SingleEventoPostuladosActivity.this,"click on item: "+ ruta_actual.routeName,Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SingleEventoPostuladosActivity.this, BuscarEventosMapaActivity.class);
                            intent.putExtra("List_Of_Points", (Serializable) camino);
                            intent.putExtra("List_Navigation", (Serializable) ruta_actual.routePointsNavigation);
                            intent.putExtra("Resume_Data", (Serializable) resumen);
                            intent.putExtra("Fecha_Hora", (singleFechaEncuentro + " " + singleHoraEncuentro).toString());
                            intent.putExtra("Start_Point", ruta_actual.routePoints.get(0));
                            intent.putExtra("Evento", singleIdEvento);
                            callLauncher.launch(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (NumberFormatException nfe) {

                }
            }
        });

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


        //Botón Cancelar Postulación
//        Además de eliminar al usuario de la lista de participantes y la referencia de postulación,
//        también elimina la referencia de pre-postulación.
        btn_CancelarPostulacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Obtener el ID del usuario actual
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String userName=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


                // Buscar el evento
                DatabaseReference eventosRef = FirebaseDatabase.getInstance().getReference().child("Eventos").child("Eventos Publicos").child(singleIdEvento);

                eventosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ModelEvento evento = dataSnapshot.getValue(ModelEvento.class);

                            // Incrementar el valor del cupoMaximo
                            int cupoMaximo = Integer.parseInt(evento.getCupoMaximo());
                            int nuevoCupoMaximo = cupoMaximo + 1;
                            evento.setCupoMaximo(String.valueOf(nuevoCupoMaximo));

                            // Obtener el ID del usuario actual
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            // Eliminar al usuario de la lista de participantes
                            evento.eliminarParticipante(userId);

                            // Eliminar la referencia de postulación del evento específico
                            DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");
                            postulacionesRef.child(userId).child(singleIdEvento).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Eliminar la referencia de pre-postulación del evento específico
                                                DatabaseReference prePostulacionesRef = FirebaseDatabase.getInstance().getReference().child("Pre-Postulaciones");
                                                prePostulacionesRef.child(singleIdEvento).child(userId).removeValue()
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
                                                                                        // Cancelación exitosa
                                                                                        Toast.makeText(getApplicationContext(), "Has cancelado tu postulación al evento", Toast.LENGTH_SHORT).show();

                                                                    //Agregar notificacion al creador de evento
                                                                    notificarPostulacionCancelada(evento.getNombreEvento(),userName,userId,evento.getIdEvento(),evento.getUserId());
                                                                    notificaPostulacionCancelada(evento.getNombreEvento(),userName,userId,evento.getIdEvento(),evento.getTokenFCM());
                                                                                    } else {
                                                                                        // Error en la modificación del evento
                                                                                        Toast.makeText(getApplicationContext(), "Error al modificar el evento", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                } else {
                                                                    // Error al eliminar la referencia de pre-postulación
                                                                    Toast.makeText(getApplicationContext(), "Error al cancelar la pre-postulación al evento", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                // Error al eliminar la referencia de postulación
                                                Toast.makeText(getApplicationContext(), "Error al cancelar la postulación al evento", Toast.LENGTH_SHORT).show();
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
        });


        //Controles para los comentarios
        txt_write_comment=findViewById(R.id.txt_write_comment);
        tv_add_comment=findViewById(R.id.tv_add_comment);
        image_profile=findViewById(R.id.image_profile);

        //recyclerViewComments
        recyclerViewComments=findViewById(R.id.recyclerViewComments);
        recycleList=new ArrayList<>();

        //Creo una instancia del adapter para los comentarios
        commentAdapter = new CommentAdapterPostulados(recycleList, SingleEventoPostuladosActivity.this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        //Seteo los listener de las interfaces de comunicacion para el adapter
        commentAdapter.setOnCommentDeleteListener(this);
        commentAdapter.setOnResponseDeleteListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewComments.setLayoutManager(linearLayoutManager);
        recyclerViewComments.addItemDecoration(new DividerItemDecoration(recyclerViewComments.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewComments.setNestedScrollingEnabled(false);
        recyclerViewComments.setAdapter(commentAdapter);


        cargarComentarios();

        //Para agregar un comentario
        tv_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txt_write_comment.getText().toString().equals("")){
                    Toast.makeText(SingleEventoPostuladosActivity.this, "No se pueden enviar mensajes vacios!!!", Toast.LENGTH_SHORT).show();
                }else {
                    addComment(singleIdEvento);
                }
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (singleUserId.equals(currentUser.getUid())) {

            ArrayList<AssistanceData> data = new ArrayList<>();
            AssistanceAdapter summaryAdapter = new AssistanceAdapter(data, SingleEventoPostuladosActivity.this, singleIdEvento, disableAssistanceButtons);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewAssistance);
            //recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(SingleEventoPostuladosActivity.this));
            recyclerView.setAdapter(summaryAdapter);

            database.getReference().child("Eventos").child("Eventos Publicos").child(singleIdEvento).child("listaParticipantes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String participante = dataSnapshot.getValue(String.class);

                            database.getReference().child("Perfil").child(participante).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUsuario usuario = snapshot.getValue(ModelUsuario.class);
                                    AssistanceData assistance;
                                    if (usuario.getImagenPerfil() != null && usuario.getNombre() != null) {
                                        assistance = new AssistanceData(usuario.getApellido(), usuario.getNombre(), usuario.getImagenPerfil(), usuario.getUserId());
                                    } else if (usuario.getImagenPerfil() != null) {
                                        assistance = new AssistanceData(usuario.getUserNameCustom(), null, usuario.getImagenPerfil(), usuario.getUserId());
                                    }
                                    else{
                                        assistance = new AssistanceData(usuario.getUserNameCustom(), null, null, usuario.getUserId());
                                    }
                                    data.add(assistance);
                                    summaryAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }
                    else
                    {
                        database.getReference().child("Eventos").child("Completados").child(singleIdEvento).child("listaParticipantes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String participante = dataSnapshot.getValue(String.class);

                                        database.getReference().child("Perfil").child(participante).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                ModelUsuario usuario = snapshot.getValue(ModelUsuario.class);
                                                AssistanceData assistance;
                                                if (usuario.getImagenPerfil() != null && usuario.getNombre() != null) {
                                                    assistance = new AssistanceData(usuario.getApellido(), usuario.getNombre(), usuario.getImagenPerfil(), usuario.getUserId());
                                                } else if (usuario.getImagenPerfil() != null) {
                                                    assistance = new AssistanceData(usuario.getUserNameCustom(), null, usuario.getImagenPerfil(), usuario.getUserId());
                                                }
                                                else{
                                                    assistance = new AssistanceData(usuario.getUserNameCustom(), null, null, usuario.getUserId());
                                                }
                                                data.add(assistance);
                                                summaryAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            TextView title = (TextView) findViewById(R.id.participantesText);
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewAssistance);
            title.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
    }//fin OnCReate

    @Override
    protected void onRestart() {
        super.onRestart();


        cargarComentarios();
    }

    private void notificarPostulacionCancelada(String nombreEvento,String userName,String postulanteId,String IdEvento,String IdOrganizador){
        NotificationCounter notificacion = new NotificationCounter();
        notificacion.registrarNotificacionPostulacionCancelada("Se cancelo postulacion de:",userName,"cancela_postulante_evento",IdEvento,postulanteId,IdOrganizador,nombreEvento);
    }

    private void notificaPostulacionCancelada(String nombreEvento,String userName,String postulanteId,String IdEvento,String TokenFCMRecuperado){

        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();

        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Se cancelo postulacion de: ");
            notificacion.put("detalle", userName);
            notificacion.put("tipo", "cancela_postulante_evento");
            notificacion.put("idEvento", IdEvento);
            notificacion.put("postulanteId", postulanteId);
            notificacion.put("tokenCreador", TokenFCMRecuperado);
            notificacion.put("nombreEvento", nombreEvento);
            json.put("to", TokenFCMRecuperado);
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

    private void cargarComentarios() {

        // Obtener el id del evento
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);
        String idEvento = sharedPreferences.getString("idEvento", "");



        //Reviso el nodo comentarios
        commentsRef = FirebaseDatabase.getInstance().getReference("ComentariosPostulacion").child(singleIdEvento);
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

    private void addComment(String idEvento) {
        // Obtengo el usuario actual que va a publicar el mensaje
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = user.getUid();
        String userName = user.getDisplayName();

        // Accedo al perfil del usuario
        DatabaseReference perfilRef = FirebaseDatabase.getInstance().getReference().child("Perfil").child(userId);
        perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Inicializo el String
                String imagenPerfilUri = "";

                if (dataSnapshot.exists()) {

                    //Obtengo el objeto que simboliza el perfil del usuario
                    ModelUsuario usuario = dataSnapshot.getValue(ModelUsuario.class);
                    String imagenPerfil = usuario.getImagenPerfil();

                    if (imagenPerfil != null && !imagenPerfil.isEmpty()) {

                        // Si hay una imagen de perfil en el perfil del usuario, la uso
                        imagenPerfilUri = imagenPerfil;

                    } else if (user.getPhotoUrl() != null) {

                        // Si no hay imagen de perfil en el perfil del usuario, pero sí en su cuenta de Google, la uso
                        imagenPerfilUri = user.getPhotoUrl().toString();

                    } else {
                        // Si no hay imagen de perfil en el perfil del usuario ni en su cuenta de Google, utilizo la imagen por defecto
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Default-Profile/doomer.jpg");
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // La URI de la imagen de perfil se ha obtenido exitosamente
                                String imagenPerfilUri = uri.toString();

                                // Ahora podemos agregar el comentario
                                agregarComentario(idEvento, userId, usuario.getUserNameCustom(), imagenPerfilUri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Manejar el error si no se pudo obtener la URI de la imagen de perfil
                                Toast.makeText(SingleEventoPostuladosActivity.this, "No se pudo obtener la imagen de perfil", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return; // Importante: Salir del método para evitar llamadas múltiples
                    }

                    // Si llegamos aquí, significa que ya tenemos la imagen de perfil y podemos agregar el comentario
                    agregarComentario(idEvento, userId, usuario.getUserNameCustom(), imagenPerfilUri);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja el error si ocurre una cancelación de la operación
            }
        });
    }

    private void agregarComentario(String idEvento, String userId, String userName, String imagenPerfilUri) {
        // Creo el nodo y los subnodos para agregar el comentario con un identificador único
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ComentariosPostulacion").child(idEvento);
        String comentarioId = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", txt_write_comment.getText().toString());
        hashMap.put("publisherId", userId);
        hashMap.put("publisherName", userName);
        hashMap.put("commentId", comentarioId);
        hashMap.put("idEvento", idEvento);
        hashMap.put("tipo", "comentario");
        hashMap.put("imagenPerfilUri", imagenPerfilUri);

        // Utilizo el identificador único para almacenar el comentario
        reference.child(comentarioId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SingleEventoPostuladosActivity.this, "Se publicó el mensaje", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(SingleEventoPostuladosActivity.this, "No se publicó el mensaje", Toast.LENGTH_SHORT).show();
                    }
                });
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ComentariosPostulacion").child(respuestaAEliminar.getEventoId());
        reference.child(respuestaId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Operación exitosa
                Toast.makeText(SingleEventoPostuladosActivity.this, "Respuesta eliminada", Toast.LENGTH_SHORT).show();

                cargarComentarios();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el error
                Toast.makeText(SingleEventoPostuladosActivity.this, "Error al eliminar respuesta", Toast.LENGTH_SHORT).show();
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
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ComentariosPostulacion").child(respuestaAEliminar.getEventoId());
            reference.child(respuestaId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    //***************Comentario*****************
                    // Obtener el ID del comentario
                    String commentId = comentarioAEliminar.getCommentId();

                    // Eliminar el comentario de la base de datos
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ComentariosPostulacion").child(comentarioAEliminar.getEventoId());
                    reference.child(commentId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Notificar al usuario que el comentario se eliminó con éxito
                            Toast.makeText(SingleEventoPostuladosActivity.this, "Comentario y respuesta eliminados", Toast.LENGTH_SHORT).show();
                            cargarComentarios();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar el error si no se puede eliminar el comentario
                            Toast.makeText(SingleEventoPostuladosActivity.this, "Error al eliminar el comentario", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Manejar el error si no se puede eliminar la respuesta
                    Toast.makeText(SingleEventoPostuladosActivity.this, "Error al eliminar la respuesta", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            //Borro comentario


            // Eliminar el comentario de la base de datos
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ComentariosPostulacion").child(comentarioAEliminar.getEventoId());
            reference.child(comentarioAEliminar.getCommentId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // Notificar al usuario que el comentario se eliminó con éxito
                    Toast.makeText(SingleEventoPostuladosActivity.this, "Comentario eliminado", Toast.LENGTH_SHORT).show();
                    cargarComentarios();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)  {
                    // Manejar el error si no se puede eliminar el comentario
                    Toast.makeText(SingleEventoPostuladosActivity.this, "Error al eliminar el comentario", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




}