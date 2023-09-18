package com.example.gps_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.gps_test.ui.ActivityBuscarEventosRecycler.ModelEvento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

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

    private String tokenFcmPostulante;





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


        // Registra el BroadcastReceiver para capturar el broadcast de aceptacion de postulacion o denegacion de postulacion
        IntentFilter filterAceptacion = new IntentFilter("com.example.sesionconfirebase.ACTION_POSTULAR");
        registerReceiver(notificationReceiver, filterAceptacion);
        IntentFilter filterDenegacion  = new IntentFilter("com.example.sesionconfirebase.ACTION_DENEGAR_POSTULACION");
        registerReceiver(notificationReceiver, filterDenegacion);




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
        String singleTokenFCM=getIntent().getStringExtra("TokenFCM");


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
        editor.putString("TokenFCM", tokenFcm);
        editor.putString("tokenFcmPostulante", tokenFcmPostulante);
        editor.putString("nombreEvento", nombreEvento);
        editor.apply();


        //Referencia al nodo postulaciones que uso dentro del boton Postularse
        DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference().child("Postulaciones");

        //Boton Postularse
        btn_postularse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Envia notificacion al creador del evento
                NotificarCreadorEvento();
            }
        });




    }//fin onCreate()

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libera el BroadcastReceiver
        unregisterReceiver(notificationReceiver);
    }


    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.example.sesionconfirebase.ACTION_POSTULAR".equals(intent.getAction())) {

                postularCandidato();
                notificarPostulanteEvento();

            } else if ("com.example.sesionconfirebase.ACTION_DENEGAR_POSTULACION".equals(intent.getAction())) {
                notificarDenegacionPostulanteEvento();
            }
        }
    };





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

    private void prePostularCandidato(){
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

        // Para recuperar el tokenFcm almacenado en SharedPreferences del creador de
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);
        String tokenFcmRecuperado = sharedPreferences.getString("TokenFCM", "");

        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();

        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Aceptar Postulacion de: ");
            notificacion.put("detalle", userName);
            notificacion.put("tipo", "creador_evento");

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



    private void notificarPostulanteEvento() {

        //"Bearer AAAA2KZHDiM:APA91bHxMVQ1jcd7sRVOqoP9ffdSEFiBnVr_iFKOL0kd_X71Arrc3lSi8is74MYUB6Iyg_1DmbvJK42Ejk-6N-i9g-yDeVjncE09U8GUOVx9YpDWjpDywU_wLXQvCO0ZERz5qZc9_zqM"

        //userName del que se postula
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        // Para recuperar el tokenFcm almacenado en SharedPreferences del creador de
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);
        String nombreEventoRecuperado = sharedPreferences.getString("nombreEvento", "");


        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();

        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Te postulaste a : ");
            notificacion.put("detalle", nombreEventoRecuperado);
            notificacion.put("tipo", "postulante_evento");

            json.put("to", tokenFcmPostulante);
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

    private void notificarDenegacionPostulanteEvento() {

        //"Bearer AAAA2KZHDiM:APA91bHxMVQ1jcd7sRVOqoP9ffdSEFiBnVr_iFKOL0kd_X71Arrc3lSi8is74MYUB6Iyg_1DmbvJK42Ejk-6N-i9g-yDeVjncE09U8GUOVx9YpDWjpDywU_wLXQvCO0ZERz5qZc9_zqM"

        //userName del que se postula
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        // Para recuperar el tokenFcm almacenado en SharedPreferences del creador de
        SharedPreferences sharedPreferences = getSharedPreferences("Evento", Context.MODE_PRIVATE);
        String nombreEventoRecuperado = sharedPreferences.getString("nombreEvento", "");


        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();

        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Te denegaron la postulacion en : ");
            notificacion.put("detalle", nombreEventoRecuperado);
            notificacion.put("tipo", "postulante_evento");

            json.put("to", tokenFcmPostulante);
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





}//Fin Appp
