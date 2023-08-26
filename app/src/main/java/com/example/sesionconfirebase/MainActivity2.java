package com.example.sesionconfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    Button btn_especifico, btn_topico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        //Manejo la logica de los botones la notificacion
        if (getIntent().hasExtra("BUTTON_CLICKED")) {
            String buttonText = getIntent().getStringExtra("BUTTON_CLICKED");
            if (buttonText.equals("Botón 1")) {
                Toast.makeText(this, "Se postuló el usuario al evento correctamente", Toast.LENGTH_SHORT).show();
            } else if (buttonText.equals("Botón 2")) {
                Toast.makeText(this, "Denegaste la postulación", Toast.LENGTH_SHORT).show();
            }
        }

        btn_topico = findViewById(R.id.btn_topico);
        btn_especifico = findViewById(R.id.btn_especifico);

        // Obtiene el token de registro de FCM y guárdalo en la base de datos
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                guardarToken(token);
            }
        });

        btn_especifico.setOnClickListener(view -> llamarEspecifico());
        btn_topico.setOnClickListener(view -> llamarATopico());
    }

    //Guardar el token en la realtimeData
    private void guardarToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("token");
        // Usar el userId de Firebase en lugar del nombre
        ref.child("leo").setValue(token);
    }

    private void llamarATopico() {
    }

    private void llamarEspecifico() {
        // Obtener el token guardado
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("token").child("leo");
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult().getValue(String.class);
                if (token != null && !token.isEmpty()) {
                    enviarNotificacion(token);
                } else {
                    Log.e("Tag", "No se encontró un token válido.");
                }
            } else {
                Log.e("Tag", "Error al obtener el token.");
            }
        });
    }

    private void enviarNotificacion(String token) {
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();

        try {
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "soy el titulo");
            notificacion.put("detalle", "soy el detalle");

            json.put("to", token);
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


}
