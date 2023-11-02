package com.example.sesionconfirebase;

import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class ModelReporteAbandonosYFinalizados {

    private String organizadorId;
    private String organizadorUsername;
    private String eventoId;
    private String nombreEvento;
    private String nombreRuta;
    private String imageUrl;

    private int totalAbandonos;
    private int totalFinalizados;
    private int totalParticipantes;

    private ArrayList<ModelEstadistica> estadisticas;


    public ModelReporteAbandonosYFinalizados() {
    }

    public ModelReporteAbandonosYFinalizados(String organizadorId, String organizadorUsername, String eventoId,
                                             String nombreEvento, String nombreRuta,String imageUrl, int totalAbandonos,
                                             int totalFinalizados, int totalParticipantes) {
        this.organizadorId = organizadorId;
        this.organizadorUsername = organizadorUsername;
        this.eventoId = eventoId;
        this.nombreEvento = nombreEvento;
        this.nombreRuta = nombreRuta;
        this.imageUrl=imageUrl;
        this.totalAbandonos = totalAbandonos;
        this.totalFinalizados = totalFinalizados;
        this.totalParticipantes = totalParticipantes;
        this.estadisticas = null;
    }



    // Otros metodos...


    public ArrayList<PieEntry> getPieChartEntries() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        float porcentajeAbandonos = calcularPorcentajeAbandonos();
        float porcentajeFinalizados = calcularPorcentajeFinalizados();

        if (porcentajeAbandonos > 0) {
            entries.add(new PieEntry(porcentajeAbandonos, "Abandonos"));
        }

        if (porcentajeFinalizados > 0) {
            entries.add(new PieEntry(porcentajeFinalizados, "Finalizados"));
        }

        return entries;
    }

    private float calcularPorcentajeAbandonos() {
        if (totalParticipantes > 0) {
            return ((float) totalAbandonos / totalParticipantes) * 100;
        } else {
            return 0;
        }
    }

    private float calcularPorcentajeFinalizados() {
        if (totalParticipantes > 0) {
            return ((float) totalFinalizados / totalParticipantes) * 100;
        } else {
            return 0;
        }
    }





    public void agregarEstadistica(ModelEstadistica estadistica) {
        if (estadisticas == null) {
            estadisticas = new ArrayList<>();
        }
        estadisticas.add(estadistica);
    }




    //Getters y Setters


    public ArrayList<ModelEstadistica> getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(ArrayList<ModelEstadistica> estadisticas) {
        this.estadisticas = estadisticas;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOrganizadorId() {
        return organizadorId;
    }

    public void setOrganizadorId(String organizadorId) {
        this.organizadorId = organizadorId;
    }

    public String getOrganizadorUsername() {
        return organizadorUsername;
    }

    public void setOrganizadorUsername(String organizadorUsername) {
        this.organizadorUsername = organizadorUsername;
    }

    public String getEventoId() {
        return eventoId;
    }

    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public int getTotalAbandonos() {
        return totalAbandonos;
    }

    public void setTotalAbandonos(int totalAbandonos) {
        this.totalAbandonos = totalAbandonos;
    }

    public int getTotalFinalizados() {
        return totalFinalizados;
    }

    public void setTotalFinalizados(int totalFinalizados) {
        this.totalFinalizados = totalFinalizados;
    }

    public int getTotalParticipantes() {
        return totalParticipantes;
    }

    public void setTotalParticipantes(int totalParticipantes) {
        this.totalParticipantes = totalParticipantes;
    }
}
