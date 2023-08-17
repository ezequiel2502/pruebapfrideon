package com.example.sesionconfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    EditText mEditTextEmail;
    EditText mEditTextPass;
    EditText mEditTextConfirmarPass;

    EditText mEditTextUsuario;
    Button mButtonRegistrar;
    TextView mTextViewRespuestaR;

    TextView mTieneCuenta;

    FirebaseAuth mAuth;

    String usuario;
    String email;
    String pass;
    String conPass;
    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mEditTextUsuario=findViewById(R.id.editTextUsuario);
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPass = findViewById(R.id.editTextPass);
        mEditTextConfirmarPass = findViewById(R.id.editConfirmarPass);
        mButtonRegistrar = findViewById(R.id.btnRegistrar);
        mTextViewRespuestaR = findViewById(R.id.textViewRespuestaR);
        mTieneCuenta=findViewById(R.id.alreadyHaveAccount);

        mProgressBar=new ProgressDialog(RegistroActivity.this);

        mAuth = FirebaseAuth.getInstance();

        mButtonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarCredenciales();
            }
        });
    }//fin OnCreate()

    public void verificarCredenciales() {
        String username = mEditTextUsuario.getText().toString();
        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPass.getText().toString();
        String confirmPass = mEditTextConfirmarPass.getText().toString();
        if (username.isEmpty() || username.length() < 5) {
            showError(mEditTextUsuario, "Username no valido");
        } else if (email.isEmpty() || !emailValido(email)) {
            showError(mEditTextEmail, "Email no valido");
        } else if (password.isEmpty() || password.length() < 7) {
            showError(mEditTextPass, "Clave no valida minimo 7 caracteres");
        } else if (confirmPass.isEmpty() || !confirmPass.equals(password)) {
            showError(mEditTextConfirmarPass, "Clave no valida, no coincide.");
        } else {
            //Mostrar ProgressBar
            mProgressBar.setTitle("Proceso de Registro");
            mProgressBar.setMessage("Registrando usuario, espere un momento");
            mProgressBar.setCanceledOnTouchOutside(false);
            mProgressBar.show();
            //Registrar usuario
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //ocultar progressBar
                        mProgressBar.dismiss();

                        //*****************Envio Mail de verificacion**************************************
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //Lo saco de la sesion para que se vuelva a loguear como corresponde con
                                //la verificacion de email, ademas esto hace que no interfiera con el control de logueo
                                //en el onStart del MainActivity
                                mAuth.signOut();
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegistroActivity.this, "Usuario registrado correctamente. Por favor, confirma que recibiste el mail de verificacion", Toast.LENGTH_SHORT).show();
                                    mEditTextEmail.setText("");
                                    mEditTextPass.setText("");

                                    //guardar datos en sharedpreferences
                                    SharedPreferences prefs = getSharedPreferences(
                                            "MyPreferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=prefs.edit();
                                    //Guarda el password
                                    editor.putString("password",password);
                                    // Guarda el username...
                                    editor.putString("username", username);
                                    //si es login con email y contraseña...
                                    editor.putBoolean("esLoginConEmailYPass", true);
                                    //si es con email y contraseña no pide la foto desde el proveedor carga una por defecto...
                                    editor.putBoolean("getPhoto", false);
                                    editor.commit();


                                    //redireccionar - intent a MainActivity...para el logueo
                                    Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                                   intent.putExtra("username", username);//lo mando al Main y desde ahi lo redirijo nuevamente al home para que este disponible cuando se loguee.intent.putExtra("password",password);//lo mando al Main y desde ahi lo redirijo nuevamente al home para que este disponible cuando se loguee.
                                   intent.putExtra("esLoginConEmailYPass",true);//lo mando al Main y desde ahi lo redirijo nuevamente al home para controlar la eliminación de la cuenta
                                    intent.putExtra("getPhoto",false);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegistroActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegistroActivity.this, "No se pudo Registrar", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }



    private void showError(EditText input, String s){
        input.setError(s);
        input.requestFocus();
    }

    private boolean emailValido(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}