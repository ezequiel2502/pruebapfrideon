package com.example.sesionconfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    Button mButtonCerrarSesion;
    Button mButtonEliminarCuenta;

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
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.btn_perfil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.btn_perfil) {
                startActivity(new Intent(getApplicationContext(), PlanificarRuta.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.btn_inicio) {
                startActivity(new Intent(getApplicationContext(), Inicio.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.btn_planificar) {
                startActivity(new Intent(getApplicationContext(), ListaEventos.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            else if (itemId == R.id.btn_Rutas) {
                startActivity(new Intent(getApplicationContext(), Rutas.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        //Tomamos los controles desde la vista
        imagenUser = findViewById(R.id.imagenUser);
        txtid = findViewById(R.id.txtId);
        txtnombres = findViewById(R.id.txtNombres);
        txtemail = findViewById(R.id.txtEmail);
        mButtonCerrarSesion = findViewById(R.id.btnCerrarSesion);
        mButtonEliminarCuenta = findViewById(R.id.btnEliminarCuenta);

        //Creamos el objeto de Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        //set datos:
        txtid.setText(currentUser.getUid());
        txtnombres.setText(currentUser.getDisplayName());
        txtemail.setText(currentUser.getEmail());

        //Una forma de recibir los intents es con el Bundle...aca un ejemplo
        //Bundle data=this.getIntent().getExtras();
        //String lalala=data.getString("nombre");

        //Recibo el intent para saber si cargo foto desde la cuenta de google  o no
        //Si se loguea con google toma la de la cuenta sino una por defecto
       boolean getPhoto = getIntent().getBooleanExtra("getPhoto", false);

        //Recibo datos
        Bundle data=this.getIntent().getExtras();
        password = data.getString("password");
        username=data.getString("username");

        //Recibo intent desde el main para saber si es registro con email y contraseña o no
        Boolean esLoginConEmailYPass=getIntent().getBooleanExtra("esLoginConEmailYPass",false);

        // Utilizar el valor recibido
        //esLoginConEmailYPass
        //!getPhoto
        if (!getPhoto) {
            //El inicio es con usuario y contraseña entonces carga una foto por defecto...no hago nada
            //Si se logueo con usuario y contraseña uso el username que proporciono y que obtuve de un intent desde el registro
            txtnombres.setText(username);

        } else {
            // El valor viene del inicio con google y obtiene la foto de la cuenta de google
            //cargar imágen con glide:
            Glide.with(this).load(currentUser.getPhotoUrl()).into(imagenUser);
        }




        //Configurar las gso para google signIn con el fin de luego desloguear de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);




        mButtonCerrarSesion.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Cerrar session con Firebase
                mAuth.signOut();
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                HomeActivity.this.finish();

            }

        });


        mButtonEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs=getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                Boolean esLoginConEmailYpass_guardado=prefs.getBoolean("esLoginConEmailYPass",true);
                if (esLoginConEmailYpass_guardado) {
                    //Para eliminar la cuenta cuando se loguea con usuario y contraseña
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                    //Prompt the user to re-provide their sign in credentials
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mAuth.signOut();
                                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else {
                                        Toast.makeText(HomeActivity.this, "No se pudo eliminar", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    //Para eliminar la cuenta cuando se loguea con el boton de google
                    //obtener el usuario actual
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    // Get the account
                    GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    if (signInAccount != null) {
                        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                        //Re-autenticar el usuario para eliminarlo
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Eliminar el usuario
                                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "Usuario Eliminado!!!", Toast.LENGTH_SHORT).show();
                                            //Log.d("dashBoard", "onSuccess:Usuario Eliminado");
                                            //llamar al metodo signOut para salir de aqui
                                            signOut();
                                        }
                                    });
                                } else {
                                    //Log.e("dashBoard", "onComplete: Error al eliminar el usuario", task.getException());
                                    Toast.makeText(getApplicationContext(), "Error al eliminar el usuario!: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        //Log.d("dashBoard", "Error: reAuthenticateUser: user account is null");
                        Toast.makeText(getApplicationContext(), "Error: reAuthenticateUser: user account is null", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });//fin onClick


    }//fin onCreate()


    private void eliminarCuenta(FirebaseUser user) {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mAuth.signOut();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "No se pudo eliminar la cuenta", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void ObtenerPerfil(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
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







