package com.example.sesionconfirebase;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SingleEventoActivity extends AppCompatActivity {
    TextView tv_SingleEvento,tv_SingleRuta,tv_SingleDescripcion,tv_SingleFechaEncuentro,tv_SingleHoraEncuentro,tv_SingleCupoMinimo,tv_SingleCupoMaximo,tv_SingleCategoria;
    ImageView singleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_evento);

        tv_SingleEvento=findViewById(R.id.tv_SingleEvento);
        tv_SingleRuta=findViewById(R.id.tv_SingleRuta);
        tv_SingleDescripcion=findViewById(R.id.tv_SingleDescripcion);
        tv_SingleFechaEncuentro=findViewById(R.id.tv_SingleFechaEncuentro);
        tv_SingleHoraEncuentro=findViewById(R.id.tv_SingleHoraEncuentro);
        tv_SingleCupoMinimo=findViewById(R.id.tv_SingleCupoMinimo);
        tv_SingleCupoMaximo=findViewById(R.id.tv_SingleCupoMaximo);
        tv_SingleCategoria=findViewById(R.id.tv_SingleCategoria);

        //Obtengo los datos de los intents

        singleImage=findViewById(R.id.singleImage);

        String imageUrl = getIntent().getStringExtra("singleImage"); // Obtén la URL de la imagen del Intent
        Glide.with(this)
                .load(imageUrl)
                .into(singleImage); // Carga la imagen en el ImageView del SingleActivity


        String singleEvento = getIntent().getStringExtra("singleEvento");
        String singleRuta = getIntent().getStringExtra("singleRuta");
        String singleDescripcion = getIntent().getStringExtra("singleDescripcion");
        String singleFechaEncuentro = getIntent().getStringExtra("singleFechaEncuentro");
        String singleHoraEncuentro = getIntent().getStringExtra("singleHoraEncuentro");
        String singleCupoMinimo = getIntent().getStringExtra("singleCupoMinimo");
        String singleCupoMaximo = getIntent().getStringExtra("singleCupoMaximo");
        String singleCategoria = getIntent().getStringExtra("singleCategoria");

        // Establece los datos en los TextViews
        tv_SingleEvento.setText(singleEvento);
        tv_SingleRuta.setText(singleRuta);
        tv_SingleDescripcion.setText(singleDescripcion);
        tv_SingleFechaEncuentro.setText(singleFechaEncuentro);
        tv_SingleHoraEncuentro.setText(singleHoraEncuentro);
        tv_SingleCupoMinimo.setText(singleCupoMinimo);
        tv_SingleCupoMaximo.setText(singleCupoMaximo);
        tv_SingleCategoria.setText(singleCategoria);



    }//fin onCreate()
}//fin App