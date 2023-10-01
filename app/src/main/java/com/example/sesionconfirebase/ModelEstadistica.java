package com.example.sesionconfirebase;

public class ModelEstadistica {

    private String organizadorId;
    private String organizadorUsername;
    private String eventoId;
    private String nombreEvento;
    private String userId;
    private String userName;
    private String imageUrl;

    private String distanciaRecorrida;
    private String tiempo;
    private String velocidadPromEvento;


    public ModelEstadistica() {
    }

    public ModelEstadistica(String organizadorId,
                            String organizadorUsername, String eventoId, String nombreEvento,
                            String userId, String userName, String imageUrl, String distanciaRecorrida,
                            String tiempo, String velocidadPromEvento) {
        this.organizadorId = organizadorId;
        this.organizadorUsername = organizadorUsername;
        this.eventoId = eventoId;
        this.nombreEvento = nombreEvento;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.distanciaRecorrida = distanciaRecorrida;
        this.tiempo = tiempo;
        this.velocidadPromEvento = velocidadPromEvento;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    public void setDistanciaRecorrida(String distanciaRecorrida) {
        this.distanciaRecorrida = distanciaRecorrida;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getVelocidadPromEvento() {
        return velocidadPromEvento;
    }

    public void setVelocidadPromEvento(String velocidadPromEvento) {
        this.velocidadPromEvento = velocidadPromEvento;
    }


}
