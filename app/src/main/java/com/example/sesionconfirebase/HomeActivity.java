package com.example.sesionconfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gps_test.BuscarEventosMapaActivity;
import com.example.gps_test.PlanificarRuta;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity {

    Button mButtonCerrarSesion;
    Button mButtonEliminarCuenta;

    Button btnIrANotificaciones;
    Button btnIrABuscarEventos;

    //controles nuevos
    ImageView profile_image;
    ImageView change_profile_image;
    TextView tv_user_name,tv_user_email,tv_UserId;
    RatingBar rating_bar;
    LinearLayout notification_bell,analytics,settings;
    ImageView add_evento;
    TextView tv_privados,tv_publicos,tv_postulados,tv_completados,tv_following;
    CardView cardView_detalles,cardView_cerrarSesion,cardView_EliminarCuenta;
    FirebaseAuth mAuth;



    private TextView txtid, txtnombres, txtemail;
    private ImageView imagenUser;

    //Variables opcionales para desloguear de google tambien
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private String password;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_2);

        //Creamos el objeto de Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Acá decimos que queremos que firebase guarde los datos de forma local aunque se pierda la conexión
        //solo hace falte activarlo una vez, preferentemente acá que entramos siempre





        //Controles nuevos
        profile_image = findViewById(R.id.profile_image);
        change_profile_image = findViewById(R.id.change_profile_image);
        tv_UserId = findViewById(R.id.tv_UserId);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_email = findViewById(R.id.tv_user_email);
        tv_completados = findViewById(R.id.tv_completados);
        cardView_detalles = findViewById(R.id.cardView_detalles);
        cardView_cerrarSesion = findViewById(R.id.cardView_cerrarSesion);
        cardView_EliminarCuenta = findViewById(R.id.cardView_EliminarCuenta);



//********************************************************************************************************************************
        //Creamos el objeto de Firebase, si paso el login,  entonces existe un currentuser
        mAuth = FirebaseAuth.getInstance();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference perfilRef = FirebaseDatabase.getInstance().getReference().child("Perfil").child(userId);

            perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ModelUsuario usuario = dataSnapshot.getValue(ModelUsuario.class);


                        // Cargamos los datos del usuario recuperado
                        tv_UserId.setText(usuario.getUserId());
                        tv_user_name.setText(usuario.getUserNameCustom());
                        tv_user_email.setText(usuario.getEmail());

                        String imagenPerfil = usuario.getImagenPerfil();

                        if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
                            // Si existe una imagen de perfil en la base de datos, la carga
                            Glide.with(HomeActivity.this).load(imagenPerfil).into(profile_image);
                        }
                        //Sino deja una imagen por defecto para el que se registro con email y contraseña(es decir no hago nada queda la de la vista)
                        else if (usuario.getEsLoginConEmailYPass()) {
                            // Si se logueó con usuario y contraseña, usa el username proporcionado
                            //tv_user_name.setText(usuario.getUserNameCustom());
                        } else if (currentUser.getPhotoUrl() != null) {
                            // Si no hay imagen de perfil en la base de datos y no se logueó con email y contraseña,
                            // carga la foto de la cuenta de Google
                            Glide.with(HomeActivity.this).load(currentUser.getPhotoUrl()).into(profile_image);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error, si es necesario
                }
            });
        }
//***********************************************************************************************************************


        tv_completados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeActivity.this,ListaEventoCompletados.class);
                startActivity(intent);
            }
        });

        //Configurar las gso para google signIn con el fin de luego desloguear de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);





        //Cambiar imagen de perfil
        change_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear un Intent para seleccionar una imagen de la galería
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });


        //nuevo metodo donde se pregunta antes de cerrar sesion
        cardView_cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar un cuadro de diálogo de confirmación
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Cerrar Sesión");
                builder.setMessage("¿Estás seguro de que quieres cerrar sesión?");

                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cerrar sesión con Firebase
                        mAuth.signOut();
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivity);
                        HomeActivity.this.finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hacer nada, simplemente cerrar el diálogo
                    }
                });

                builder.show();
            }
        });



        //Agrega una pregunta antes de eliminar y no solo quita la cuenta
        // del servicio de autenticacion sino que elimina el perfil de la base de datos
        cardView_EliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Eliminar Cuenta");
                builder.setMessage("¿Estás seguro de que quieres eliminar tu cuenta?");

                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            DatabaseReference perfilRef = FirebaseDatabase.getInstance().getReference().child("Perfil").child(userId);

                            perfilRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        ModelUsuario usuario = dataSnapshot.getValue(ModelUsuario.class);

                                        // Elimina el perfil de la base de datos
                                        dataSnapshot.getRef().removeValue();

                                        // Desconecta al usuario
                                        mAuth.signOut();

                                        if (usuario != null) {
                                            if (usuario.getEsLoginConEmailYPass()) {
                                                // Código para eliminar cuenta con usuario y contraseña
                                                AuthCredential credential = EmailAuthProvider.getCredential(usuario.getEmail(), usuario.getPass());
                                                currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        // Ahora, una vez que se ha eliminado el usuario correctamente,
                                                                        // puedes ir a la pantalla de inicio
                                                                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        startActivity(intent);
                                                                    } else {
                                                                        Toast.makeText(HomeActivity.this, "No se pudo eliminar", Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            } else {
                                                // Código para eliminar cuenta con inicio de sesión de Google
                                                GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                                if (signInAccount != null) {
                                                    AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                                                    currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(getApplicationContext(), "Usuario Eliminado!!!", Toast.LENGTH_SHORT).show();
                                                                        signOut();
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Error al eliminar el usuario!: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Error: reAuthenticateUser: user account is null", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Manejar el error, si es necesario
                                }
                            });
                        }
                    }
                });


                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No hacer nada, simplemente cerrar el diálogo
                    }
                });

                builder.show();
            }
        });




        //LLeva a la actividad que guarda detalles del usuario
        cardView_detalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        /*btnIrABuscarEventos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BuscarEventosMapaActivity.class);
                startActivity(intent);
            }
        });*/


        //Barra de navegacion

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.btn_perfil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_perfil) {
                Intent intent = new Intent(getApplicationContext(), PlanificarRuta.class);
                intent.putExtra("Close_On_Enter", "False");
                intent.putExtra("User_ID", currentUser.getUid());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            } else if (itemId == R.id.btn_inicio) {
                startActivity(new Intent(getApplicationContext(), ListaEventosPublicosVigentes.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            } else if (itemId == R.id.btn_planificar) {
                startActivity(new Intent(getApplicationContext(), ListaEventos.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            }
            else if (itemId == R.id.btn_lista_postulados) {
                startActivity(new Intent(getApplicationContext(), ListaEventoPostulados.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            }
            else if (itemId == R.id.btn_lista_rutas) {
                startActivity(new Intent(getApplicationContext(), Rutas.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                return true;
            }
            return false;
        });

    }//fin onCreate()


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            //El usuario logueado
            String userId = mAuth.getCurrentUser().getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                    .child("Perfil").child(userId);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ModelUsuario usuario = dataSnapshot.getValue(ModelUsuario.class);
                        String userName=usuario.getUserNameCustom();

                        if (usuario != null) {
                            // Verificar si el usuario tiene el atributo imagenPerfil
                            if (usuario.getImagenPerfil() != null) {
                                // El usuario tiene una imagen de perfil almacenada, eliminarla del Storage
                                StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(usuario.getImagenPerfil());
                                oldImageRef.delete().addOnSuccessListener(aVoid -> {
                                    // Subir la nueva imagen
                                    subirNuevaImagen(uri, userId,userName);
                                });
                            } else {
                                // No hay una imagen de perfil anterior, simplemente subir la nueva imagen
                                subirNuevaImagen(uri, userId,userName);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error, si es necesario
                }
            });
        }
    }

    private void subirNuevaImagen(Uri uri, String userId,String userName) {

        //Elijo el nombre con el que guardo la foto(username + timeStamp)
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageName = userName + timeStamp + ".jpg";

        //Obtengo la referencia al storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Perfil").child(userId).child(imageName);

        storageReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        String imageUrl = uri1.toString();

                        // Actualizar el atributo imagenPerfil en la base de datos
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Perfil").child(userId).child("imagenPerfil");
                        databaseReference.setValue(imageUrl);

                        // Cargar la imagen en el ImageView usando Glide
                        Glide.with(HomeActivity.this).load(uri).into(profile_image);

                        Toast.makeText(HomeActivity.this, "Imagen de perfil actualizada", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                });
    }




    private void signOut() {
        //sign out de firebase
        FirebaseAuth.getInstance().signOut();
        //sign out de "google sign in"
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override public void onComplete(@NonNull Task<Void> task) {
                //regresar al login screen o MainActivity
                // Abrir mainActivity para que inicie sesión o sign in otra vez.
                Intent IntentMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                IntentMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(IntentMainActivity); HomeActivity.this.finish();
            }
        });
    }

}







