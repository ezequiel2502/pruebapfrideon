package com.example.sesionconfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    //Variable mAuthStateListener para controlar el estado del usuario:
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;

//controles activity_main
//    EditText mEditTextEmail;
//    EditText mEditTextPass;
//    Button mButtonInicio;
//    TextView mTextViewIrRegistrar;
//    TextView mTextViewRespuesta;
//    TextView mTextViewForgotPassword;
//    SignInButton mSignInButtonGoogle;

    FirebaseAuth mAuth;

    String email;
    String pass;

    String username;
    String password;

    Boolean esLoginConEmailYPass;

    String msjInicio;

    private ProgressDialog mProgressBar;

    //**************Controles login2**************

    CardView cardview_registrarseUsuarioYPass;
    CardView cardview_registrarseGoogle;
    EditText editTextEmail;
    EditText editTextPass;
    TextView textViewRespuesta;
    private static boolean first_Entrance = false;
    Button btn_olvidaste_contrasena,btn_iniciar_sesion;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login2);
        if (first_Entrance == false) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            first_Entrance = true;
        }
        //*********Controles de activity_main
//        mEditTextEmail = findViewById(R.id.editTextEmail);
//        mEditTextPass = findViewById(R.id.editTextPass);
//        mButtonInicio = findViewById(R.id.btnInicio);
//        mTextViewRespuesta = findViewById(R.id.textViewRespuesta);
//        mTextViewIrRegistrar = findViewById(R.id.textViewIrRegistrar);
//        mTextViewForgotPassword=findViewById(R.id.forgotPassword);
//        mSignInButtonGoogle = findViewById(R.id.btnGoogle);
//        mTextViewRespuesta = findViewById(R.id.textViewRespuesta);


        //************controles login2
        cardview_registrarseUsuarioYPass=findViewById(R.id.cardview_registrarseUsuarioYPass);
        cardview_registrarseGoogle=findViewById(R.id.cardview_registrarseGoogle);
        btn_olvidaste_contrasena=findViewById(R.id.btn_olvidaste_contrasena);
        btn_iniciar_sesion=findViewById(R.id.btn_iniciar_sesion);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPass=findViewById(R.id.editTextPass);
        textViewRespuesta=findViewById(R.id.textViewRespuesta);




        mProgressBar = new ProgressDialog(MainActivity.this);

        //Objeto de firebase
        mAuth = FirebaseAuth.getInstance();



        //*****Configuraciones de firebase para google SIGNIN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //******Configuraciones de firebase para google SIGNIN


        //Obtengo el intent desde el RegistroActivity para conservar el username y el password
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        esLoginConEmailYPass=getIntent().getBooleanExtra("esLoginConEmailYPass",false);



          //Para registrarse por primera vez, te manda al RegistroActivity
        cardview_registrarseUsuarioYPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        //Para reestablecer contraseña
        btn_olvidaste_contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        //Para loguearse una veza registrado...
        btn_iniciar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarCredenciales();
            }
        });

        //Le damos fuuncionalidad al boton de GOOGLE, que llama al método  que esta abajo signIn..
        cardview_registrarseGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esLoginConEmailYPass=false;
                signIn();
            }
        });



       /*mAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                //&& !esRegistroConEMailYPass
                if (firebaseAuth.getCurrentUser() != null)
                {
                    //si no es null redirigir
                    Intent intentDashboard = new Intent(getApplicationContext(), HomeActivity.class);
                    intentDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentDashboard);
                }
            }
        };*/

    }//fin OnCreate

    public void verificarCredenciales(){
        String email = editTextEmail.getText().toString();
        String password = editTextPass.getText().toString();
        if(email.isEmpty() || !emailValido(email)){
            showError(editTextEmail, "Email no valido");
        }else if(password.isEmpty()|| password.length()<7){
            showError(editTextPass, "Password invalida");
        }else{
            //Mostrar ProgressBar
            mProgressBar.setTitle("Login");
            mProgressBar.setMessage("Iniciando sesión, espere un momento..");
            mProgressBar.setCanceledOnTouchOutside(false);
            mProgressBar.show();
            //Registrar usuario
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        if(mAuth.getCurrentUser().isEmailVerified()){


                            //ocultar progressBar
                            mProgressBar.dismiss();

                            //Obtengo los datos que guardaron en sharedpreferences del RegistroActivity...
                            SharedPreferences prefs=getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
                            String username_guardado=prefs.getString("username","");
                            String password_guardada=prefs.getString("password","");
                            String email_guardado=prefs.getString("email","");
                            Boolean esLoginConEmailYpass_guardado=prefs.getBoolean("esLoginConEmailYPass",true);

                            textViewRespuesta.setText("CORRECTO");
                            textViewRespuesta.setTextColor(Color.GREEN);

                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


                            // Obtener el token de FCM
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (task.isSuccessful() && task.getResult() != null) {

                                                //Este es el token que pido desde firebase,comparo este con el que esta en la base de datos cada vez que me logueo
                                                String tokenFcm = task.getResult();

                                                // Verificar si existe un token FCM en la base de datos
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                                DatabaseReference perfilRef = databaseReference.child("Perfil").child(userId);

                                                perfilRef.child("tokenFcm").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String tokenFromDatabase = dataSnapshot.getValue(String.class);

                                                        // si el token cambio lo actualizo en la base de datos
                                                        if (tokenFromDatabase != null && !tokenFromDatabase.equals(tokenFcm)) {
                                                            perfilRef.child("tokenFcm").setValue(tokenFcm);
                                                        }



                                                        // Resto del código para redirigir o realizar otras acciones

                                                        // Guardar datos en SharedPreferences
                                                        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = prefs.edit();
                                                        editor.putString("email", email);
                                                        editor.putString("username", username);
                                                        editor.putString("userId", userId);
                                                        editor.putBoolean("esLoginConEmailYPass", true); // Esto indica que se hizo login con email y contraseña
                                                        editor.putBoolean("getPhoto", false);
                                                        editor.putString("fcmToken", tokenFcm);
                                                        editor.apply();


                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        // Manejar el error, si es necesario
                                                    }
                                                });
                                            }
                                        }
                                    });

                            //redireccionar - intent a HomeActivity...
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("getPhoto", false);
                            intent.putExtra("username", username_guardado);//envio el username
                            intent.putExtra("password", password_guardada);//envio el password
                            intent.putExtra("userId", userId);//envio el userId
                            intent.putExtra("esLoginConEmailYPass",esLoginConEmailYpass_guardado);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {
                            mProgressBar.dismiss();
                            Toast.makeText(MainActivity.this, "Por favor verifica tu correo!!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        mProgressBar.dismiss();
                        textViewRespuesta.setText("No se pudo iniciar Sesión. Verifica correo/contraseña");
                        textViewRespuesta.setTextColor(Color.RED);
                        Toast.makeText(MainActivity.this, "No se pudo iniciar Sesión. Verifica correo/contraseña", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void showError(EditText input, String s){
        input.setError(s);
        input.requestFocus();
    }

    private void irHome(){
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("msjInicio",msjInicio);
        startActivity(intent);
        finish();
    }

    private boolean emailValido(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    protected void onStart()
    {
        //mAuth.addAuthStateListener(mAuthStateListener);
        //mAuth.signOut();
        //Sirve para: Si me salgo de la aplicacion y hay un usuario logueado que cuando entre siga logueado en el home
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            //obtengo datos del sharedPreferences que se guardaron en RegistroActivity
            SharedPreferences prefs=getSharedPreferences("MyPreferences",Context.MODE_PRIVATE);
            String username_guardado=prefs.getString("username","");
            String password_guardada=prefs.getString("password","");
            Boolean esLoginConEmailYpass_guardado=prefs.getBoolean("esLoginConEmailYPass",true);
            Boolean getPhoto_guardado=prefs.getBoolean("getPhoto",true);

            //Una vez que los obtuve los envio al Home de vuelta para que esten accesibles todos los datos...
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("username", username_guardado);//envio el username
            intent.putExtra("password", password_guardada);//envio el password
            intent.putExtra("esLoginConEmailYPass",esLoginConEmailYpass_guardado);
            intent.putExtra("getPhoto",getPhoto_guardado);
            intent.putExtra("userId",user.getUid());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        }
        super.onStart();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                textViewRespuesta.setText(e.getMessage());
            }
        }
    }

    // ...

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Obtener datos del usuario de la cuenta de Google
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                            String username = account.getDisplayName();
                            String email = account.getEmail();
                            String userId = mAuth.getCurrentUser().getUid();

                            // Obtener el token de FCM
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (task.isSuccessful() && task.getResult() != null) {

                                                //Si lo obtuvo lo almaceno
                                                String tokenFcm= task.getResult();

                                               // Obtener una referencia a la base de datos
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                                // Verificar si existe un usuario con ese ID
                                                databaseReference.child("Perfil").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            // El usuario ya existe, verificar y actualizar el tokenFcm
                                                            String tokenFromDatabase = dataSnapshot.child("tokenFcm").getValue(String.class);
                                                            if (tokenFromDatabase != null && !tokenFromDatabase.equals(tokenFcm)) {
                                                                // Actualizar el token FCM en la base de datos
                                                                databaseReference.child("Perfil").child(userId).child("tokenFcm").setValue(tokenFcm);
                                                            }
                                                        } else {
                                                            // El usuario no existe, crear uno nuevo
                                                            ModelUsuario usuario = new ModelUsuario(email, null, username, userId, tokenFcm, false);
                                                            databaseReference.child("Perfil").child(userId).setValue(usuario);
                                                        }




                                                        // Guardar datos en SharedPreferences
                                                        SharedPreferences prefs = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = prefs.edit();
                                                        editor.putString("email", email);
                                                        editor.putString("username", username);
                                                        editor.putString("userId", userId);
                                                        editor.putBoolean("esLoginConEmailYPass", false);
                                                        editor.putBoolean("getPhoto", true);
                                                        editor.putString("fcmToken", tokenFcm);
                                                        editor.apply();

                                                        // Redireccionar a HomeActivity
                                                        Intent home = new Intent(MainActivity.this, HomeActivity.class);
                                                        home.putExtra("getPhoto", true);
                                                        home.putExtra("esLoginConEmailYPass", false);
                                                        startActivity(home);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        // Manejar el error, si es necesario
                                                    }
                                                });
                                            }
                                        }
                                    });
                        } else {
                            textViewRespuesta.setText(task.getException().toString());
                        }
                    }
                });
    }

// ...




}