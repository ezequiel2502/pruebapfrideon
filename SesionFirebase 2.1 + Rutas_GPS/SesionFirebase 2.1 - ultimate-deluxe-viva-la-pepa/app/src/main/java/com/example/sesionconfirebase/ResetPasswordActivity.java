package com.example.sesionconfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText mEditTextEmail;
    Button mBtnResetPassword;

    private String email="";

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mEditTextEmail=findViewById(R.id.editTextEmail);
        mBtnResetPassword=findViewById(R.id.btnResetPassword);

        mProgressBar = new ProgressDialog(ResetPasswordActivity.this);

        mAuth=FirebaseAuth.getInstance();

        mBtnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email=mEditTextEmail.getText().toString();
                if(email.isEmpty() || !emailValido(email)){
                    showError(mEditTextEmail, "Email no valido");

                }else{
                    mProgressBar.setTitle("Verificando");
                    mProgressBar.setMessage("Espere un momento...");
                    mProgressBar.setCanceledOnTouchOutside(false);
                    mProgressBar.show();
                    resetPassword();
                }


            }
        });




    }

    private void resetPassword() {

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    mProgressBar.dismiss();
                    Toast.makeText(ResetPasswordActivity.this, "Email de reestablecimiento enviado.Verificá tu casilla.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else {
                    mProgressBar.dismiss();
                    Toast.makeText(ResetPasswordActivity.this, "Fallo al enviar email de reestablecimiento", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean emailValido(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void showError(EditText input, String s){
        input.setError(s);
        input.requestFocus();
    }
}