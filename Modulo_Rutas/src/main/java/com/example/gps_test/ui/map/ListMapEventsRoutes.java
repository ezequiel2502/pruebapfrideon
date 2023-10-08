package com.example.gps_test.ui.map;

import android.graphics.Bitmap;

import com.example.gps_test.Ruta;
import com.example.gps_test.ui.ActivityBuscarEventosRecycler.ModelEvento;

public class ListMapEventsRoutes {

    private Long markerId;
    private ModelEvento evento;
    private Ruta ruta;
    private Bitmap imagenEvento;

    public ListMapEventsRoutes(Long markerId, ModelEvento evento, Ruta ruta, Bitmap imagenEvento) {
        this.markerId = markerId;
        this.evento = evento;
        this.ruta = ruta;
        this.imagenEvento = imagenEvento;
    }

    public Long getMarkerId() {
        return markerId;
    }

    public void setMarkerId(Long markerId) {
        this.markerId = markerId;
    }

    public ModelEvento getEvento() {
        return evento;
    }

    public void setEvento(ModelEvento evento) {
        this.evento = evento;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Bitmap getImagenEvento() {
        return imagenEvento;
    }

    public void setImagenEvento(Bitmap imagenEvento) {
        this.imagenEvento = imagenEvento;
    }
}
