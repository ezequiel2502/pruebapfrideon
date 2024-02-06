package com.example.sesionconfirebase;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class fcmHttpV1 extends AppCompatActivity {

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
            JSONObject message = new JSONObject();

            JSONObject notification = new JSONObject();
            notification.put("titulo", "Aceptar Postulacion de: ");
            notification.put("detalle", userName);
            notification.put("tipo", "creador_evento");
            notification.put("idEvento", idEvento);
            notification.put("postulanteId", postulanteId);
            notification.put("tokenCreador", tokenFcmRecuperado);
            notification.put("tokenPostulante", tokenFcmPostulante);
            notification.put("nombreEvento", nombreEvento);


            message.put("token", tokenFcmRecuperado);
            message.put("data", notification); // Cambio de "data" a "notification"
            json.put("message", message); //Se tiene que agregar este encabezado si o si

            // URL que se utilizará para enviar la solicitud POST al servidor de FCM
            String URL = "https://fcm.googleapis.com/v1/projects/tutorial-sesion-firebase/messages:send";
            Response.Listener<JSONObject> retriever = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //Acá podemos ver si el envio fue exitoso
                }
            };

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, retriever, null) {
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



    private void notificarPostulanteEvento( Context context,String nombreEvento, String tokenPostulante) {

        RequestQueue myrequest = Volley.newRequestQueue(context);
        JSONObject json = new JSONObject();

        try {
            JSONObject message = new JSONObject();

            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Aceptaron tu postulacion a : ");
            notificacion.put("detalle", nombreEvento);
            notificacion.put("tipo", "postulante_evento");

            message.put("to", tokenPostulante);
            message.put("data", notificacion); // Cambio de "data" a "notification"
            json.put("message", message);

            // URL que se utilizará para enviar la solicitud POST al servidor de FCM
            String URL = "https://fcm.googleapis.com/v1/projects/tutorial-sesion-firebase/messages:send";

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

    private void notificarDenegacionPostulanteEvento( Context context,String nombreEvento, String tokenPostulante) {

        RequestQueue myrequest = Volley.newRequestQueue(context);
        JSONObject json = new JSONObject();

        try {
            JSONObject message = new JSONObject();

            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo", "Denegaron tu postulacion a : ");
            notificacion.put("detalle", nombreEvento);
            notificacion.put("tipo", "postulante_evento");

            message.put("to", tokenPostulante);
            message.put("data", notificacion); // Cambio de "data" a "notification"
            json.put("message", message);


            // URL que se utilizará para enviar la solicitud POST al servidor de FCM
            String URL = "https://fcm.googleapis.com/v1/projects/tutorial-sesion-firebase/messages:send";

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
