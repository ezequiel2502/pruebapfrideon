package com.example.sesionconfirebase;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RespuestaComentarioActivity extends AppCompatActivity {

    EditText txt_respuesta;
    Button btn_enviar_respuesta;

    ImageView image_profile;

    TextView tv_userName,tv_comment;

    String commentId;
    String userName;
    String userId;
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
        commentId = intent.getStringExtra("commentId");
        String idEvento = intent.getStringExtra("idEvento");


        //********************************- Inicio carga del comentario a Responder -****************************************
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


        //********************************- Fin carga del comentario a Responder -********************************************




                                                        //*****^*****




        //********************************- Inicio Acceso al perfil del usuario -********************************************


        //Usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        DatabaseReference perfilRef = FirebaseDatabase.getInstance().getReference().child("Perfil").child(userId);
        perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ModelUsuario usuario = dataSnapshot.getValue(ModelUsuario.class);
                    userName = usuario.getUserNameCustom();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Maneja el error si ocurre una cancelación de la operación
            }
        });

        //********************************- Fin Acceso al perfil del usuario -********************************************



        btn_enviar_respuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //La respuesta
                String respuesta = txt_respuesta.getText().toString();


                // Accedo al perfil del usuario
                DatabaseReference perfilRef = FirebaseDatabase.getInstance().getReference().child("Perfil").child(userId);
                perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Inicializo el String
                        String imagenPerfilUri = "";

                        if (dataSnapshot.exists()) {
                            // Obtengo el objeto que simboliza el perfil del usuario
                            ModelUsuario usuario = dataSnapshot.getValue(ModelUsuario.class);
                            String imagenPerfil = usuario.getImagenPerfil();

                            if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
                                // Si hay una imagen de perfil en el perfil del usuario, la uso
                                imagenPerfilUri = imagenPerfil;
                            } else if (user.getPhotoUrl() != null) {
                                // Si no hay imagen de perfil en el perfil del usuario, pero sí en su cuenta de Google, la uso
                                imagenPerfilUri = user.getPhotoUrl().toString();
                            }
                        }

                        if (imagenPerfilUri.isEmpty()) {
                            // Si no se pudo obtener la imagen de perfil, utiliza la URL por defecto
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Default-Profile/doomer.jpg");
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // La URI de la imagen de perfil se ha obtenido exitosamente
                                    String imagenPerfilUri = uri.toString();

                                    // Ahora podemos agregar la respuesta
                                    agregarRespuesta(idEvento, userId, respuesta, userName, imagenPerfilUri);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Manejar el error si no se pudo obtener la URI de la imagen de perfil
                                    Toast.makeText(RespuestaComentarioActivity.this, "No se pudo obtener la imagen de perfil", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Si ya tenemos la imagen de perfil, podemos agregar la respuesta
                            agregarRespuesta(idEvento, userId, respuesta, userName, imagenPerfilUri);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Maneja el error si ocurre una cancelación de la operación
                    }
                });
            }
        });





    }//fin onCreate()

    private void agregarRespuesta(String idEvento, String userId, String respuesta, String userName, String imagenPerfilUri) {
        // Agrega la lógica para almacenar la respuesta en la base de datos y realizar otras operaciones necesarias
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comentarios").child(idEvento);
        String comentarioId = reference.push().getKey(); // Genera un ID único para la respuesta
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", respuesta);
        hashMap.put("publisherId", userId);
        hashMap.put("publisherName", userName);
        hashMap.put("commentId", comentarioId);
        hashMap.put("idEvento", idEvento);
        hashMap.put("tipo", "respuesta");
        hashMap.put("imagenPerfilUri", imagenPerfilUri); // Agrega la URI de la imagen de perfil del usuario actual
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
}