package com.example.sesionconfirebase;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.Calendar;

public class DetallesActivity extends AppCompatActivity {

    private int dia,mes,ano,hora,minutos;
    Button btn_FechaNacimiento;
    EditText editTextName,editTextLastName,txt_FechaNacimiento,editTextCity,editTextCountry,editTextFacebook,editTextTwitter,editTextInstagram;

    CardView cardView_guardarDetalles;
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
        cardView_guardarDetalles=findViewById(R.id.cardView_guardarDetalles);




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


        cardView_guardarDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


}//fin onCreate()

}//fin App
