package com.example.sesionconfirebase;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RespuestaComentarioActivity extends AppCompatActivity {

    EditText txt_respuesta;
    Button btn_enviar_respuesta;

    ImageView image_profile;

    TextView tv_userName,tv_comment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_comentario);

        // Recuperar los datos pasados a través del Intent del comentario al que se responde
        Intent intent = getIntent();
        String comentario = intent.getStringExtra("comment");
        String publisherId = intent.getStringExtra("publisherId");
        String publisherName = intent.getStringExtra("publisherName");
        String imagenPerfilUri = intent.getStringExtra("imagenPerfilUri");
        String commentId = intent.getStringExtra("commentId");
        String idEvento = intent.getStringExtra("idEvento");


        txt_respuesta=findViewById(R.id.txt_respuesta);
        btn_enviar_respuesta=findViewById(R.id.btn_enviar_respuesta);
        image_profile=findViewById(R.id.image_profile);
        tv_userName=findViewById(R.id.tv_userName);
        tv_comment=findViewById(R.id.tv_comment);


        // Cargar la imagen usando Glide desde la URI almacenada en la base de datos
        String imagePath = imagenPerfilUri;
        Glide.with(getApplicationContext())
                .load(imagePath) // Carga la imagen desde la URI
                .into(image_profile);

        //Se cargan el resto de campos
        tv_comment.setText(comentario);
        tv_userName.setText(publisherName);

        btn_enviar_respuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //La respuesta
                String respuesta = txt_respuesta.getText().toString();

                // Obtiene el ID del usuario actual
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();


                // Agrega la lógica para almacenar la respuesta en la base de datos y realizar otras operaciones necesarias
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ComentariosPostulacion").child(idEvento);
                String comentarioId = reference.push().getKey(); // Genera un ID único para la respuesta
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("comment", respuesta);
                hashMap.put("publisherId", userId);
                hashMap.put("publisherName", user.getDisplayName());
                hashMap.put("commentId", comentarioId);
                hashMap.put("idEvento", idEvento);
                hashMap.put("tipo", "respuesta");
                hashMap.put("imagenPerfilUri", user.getPhotoUrl().toString()); // Agrega la URI de la imagen de perfil del usuario actual
                hashMap.put("parentCommentId", commentId); // Agrega la referencia al comentario al que se responde

                reference.child(comentarioId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RespuestaComentarioActivity.this, "Se publicó la respuesta", Toast.LENGTH_SHORT).show();
                        // Cierra la actividad actual después de publicar la respuesta


                        finish();



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RespuestaComentarioActivity.this, "No se pudo publicar la respuesta", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });


    }
}