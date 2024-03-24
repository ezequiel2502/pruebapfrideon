package com.example.sesionconfirebase;

import static com.example.sesionconfirebase.Utils.GetNotifications;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.Calendar;

public class DetallesActivity extends AppCompatActivity {

    private int dia,mes,ano,hora,minutos;
    Button btn_FechaNacimiento, btn_guardarDetalles;
    EditText editTextName,editTextLastName,txt_FechaNacimiento,editTextCity,editTextCountry,editTextFacebook,editTextTwitter,editTextInstagram;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    NotificationBadge notificactionBadge;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);


        //tomo los controles de la vista
        editTextName=findViewById(R.id.editTextName);
        editTextLastName=findViewById(R.id.editTextLastName);
        btn_FechaNacimiento=findViewById(R.id.btn_FechaNacimiento);
        txt_FechaNacimiento=findViewById(R.id.txt_FechaNacimiento);
        editTextCity=findViewById(R.id.editTextCity);
        editTextCountry=findViewById(R.id.editTextCountry);
        editTextFacebook=findViewById(R.id.editTextFacebook);
        editTextTwitter=findViewById(R.id.editTextTwitter);
        editTextInstagram=findViewById(R.id.editTextInstagram);
        editTextInstagram=findViewById(R.id.editTextInstagram);
        btn_guardarDetalles=findViewById(R.id.btn_guardarDetalles);

        notificactionBadge=findViewById(R.id.badge);
        toolbar=findViewById(R.id.main_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

        //si habia datos ya cargados en el perfil los muestra en los campos
        // El objeto de firebase
        mAuth = FirebaseAuth.getInstance();

        // Obtener el ID del usuario actualmente logueado
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        GetNotifications(currentUser, notificactionBadge);

        // Obtener una referencia a la base de datos
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Verificar si el perfil ya existe
        databaseReference.child("Perfil").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El perfil ya existe, obtener los datos
                    ModelUsuario usuario = dataSnapshot.getValue(ModelUsuario.class);

                    // Asignar los datos a los EditText
                    editTextName.setText(usuario.getNombre());
                    editTextLastName.setText(usuario.getApellido());
                    txt_FechaNacimiento.setText(usuario.getFechaNac());
                    editTextCity.setText(usuario.getCiudad());
                    editTextCountry.setText(usuario.getPais());
                    editTextFacebook.setText(usuario.getFacebook());
                    editTextTwitter.setText(usuario.getTwitter());
                    editTextInstagram.setText(usuario.getInstagram());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar el error, si es necesario
            }
        });


        //DatePicker
        btn_FechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                dia = c.get(Calendar.DAY_OF_MONTH);
                mes = c.get(Calendar.MONTH);
                ano = c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(DetallesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayofMonth) {
                        txt_FechaNacimiento.setText(dayofMonth+"/"+(monthOfYear+1)+"/"+year);
                    }
                }, ano, mes, dia); // Pasar los valores de año, mes y día para mostrar la fecha actual al abrir el diálogo
                datePickerDialog.show();
            }
        });


        btn_guardarDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Obtener el ID de usuario actualmente autenticado
                String userId = mAuth.getCurrentUser().getUid();

                // Obtener los detalles de la vista
                String nombre = editTextName.getText().toString();
                String apellido = editTextLastName.getText().toString();
                String fechaNacimiento = txt_FechaNacimiento.getText().toString();
                String ciudad = editTextCity.getText().toString();
                String pais = editTextCountry.getText().toString();
                String facebook = editTextFacebook.getText().toString();
                String twitter = editTextTwitter.getText().toString();
                String instagram = editTextInstagram.getText().toString();



                // Obtener una referencia a la base de datos
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                // Verificar si el perfil ya existe
                databaseReference.child("Perfil").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // El perfil ya existe, actualizar los campos
                            ModelUsuario usuario = dataSnapshot.getValue(ModelUsuario.class);
                            usuario.setNombre(nombre);
                            usuario.setApellido(apellido);
                            usuario.setFechaNac(fechaNacimiento);
                            usuario.setCiudad(ciudad);
                            usuario.setPais(pais);
                            usuario.setFacebook(facebook);
                            usuario.setTwitter(twitter);
                            usuario.setInstagram(instagram);

                            // Actualizar el perfil en la base de datos
                            databaseReference.child("Perfil").child(userId).setValue(usuario);
                            Toast.makeText(DetallesActivity.this, "Perfil actualizado correctamente", Toast.LENGTH_LONG).show();
                        } else {
                            //No existe un perfil o no se encuentra
                            Toast.makeText(DetallesActivity.this, "No existe un perfil o no se encuentra", Toast.LENGTH_LONG).show();
                        }

                        // Resto del código después de actualizar o crear el perfil

                    }@Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar el error, si es necesario
                    }
                });

            }
        });


}//fin onCreate()

    public void redirectToOtherActivity(View view) {
        // Crea un Intent para abrir la otra actividad
        Intent intent = new Intent(this, ListadoNotificacionesActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}//fin App
