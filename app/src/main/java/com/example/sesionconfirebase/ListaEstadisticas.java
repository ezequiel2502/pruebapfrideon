package com.example.sesionconfirebase;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListaEstadisticas extends AppCompatActivity {



    RecyclerView recyclerViewEstadisticasEventos;
    ArrayList<ModelEvento> recycleList;

    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_estadisticas);


    }
}
