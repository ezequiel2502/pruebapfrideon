package com.example.sesionconfirebase;

public class ModelEstadistica {

    private String organizadorId;
    private String organizadorUsername;
    private String eventoId;
    private String nombreEvento;
    private String nombreRuta;
    private String userId;
    private String userName;
    private String imageUrl;
    private String distanciaRecorrida;
    private String tiempo;
    private String velocidadPromEvento;
    private Boolean abandono;
    private Boolean finalizo;


    public ModelEstadistica() {
    }

    public ModelEstadistica(String organizadorId,
                            String organizadorUsername, String eventoId, String nombreEvento,String nombreRuta,
                            String userId, String userName, String imageUrl, String distanciaRecorrida,
                            String tiempo, String velocidadPromEvento) {
        this.organizadorId = organizadorId;
        this.organizadorUsername = organizadorUsername;
        this.eventoId = eventoId;
        this.nombreEvento = nombreEvento;
        this.nombreRuta = nombreRuta;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.distanciaRecorrida = distanciaRecorrida;
        this.tiempo = tiempo;
        this.velocidadPromEvento = velocidadPromEvento;
        this.abandono=null;
        this.finalizo=null;
    }

    public ModelEstadistica(String organizadorId,
                            String organizadorUsername, String eventoId, String nombreEvento,String nombreRuta,
                            String userId, String userName, String imageUrl, String distanciaRecorrida,
                            String tiempo, String velocidadPromEvento, Boolean abandono,Boolean finalizo) {
        this.organizadorId = organizadorId;
        this.organizadorUsername = organizadorUsername;
        this.eventoId = eventoId;
        this.nombreEvento = nombreEvento;
        this.nombreRuta = nombreRuta;
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.distanciaRecorrida = distanciaRecorrida;
        this.tiempo = tiempo;
        this.velocidadPromEvento = velocidadPromEvento;
        this.abandono=abandono;
        this.finalizo=finalizo;
    }

    //Getters


    public Boolean getAbandono() {
        return abandono;
    }

    public Boolean getFinalizo() {
        return finalizo;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public String getOrganizadorUsername() {
        return organizadorUsername;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getOrganizadorId() {
        return organizadorId;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDistanciaRecorrida() {
        return distanciaRecorrida;
    }

    public String getTiempo() {
        return tiempo;
    }

    public String getVelocidadPromEvento() {
        return velocidadPromEvento;
    }


    //Setters


    public void setAbandono(Boolean abandono) {
        this.abandono = abandono;
    }

    public void setFinalizo(Boolean finalizo) {
        this.finalizo = finalizo;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public void setOrganizadorId(String organizadorId) {
        this.organizadorId = organizadorId;
    }

    public void setOrganizadorUsername(String organizadorUsername) {
        this.organizadorUsername = organizadorUsername;
    }

    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDistanciaRecorrida(String distanciaRecorrida) {
        this.distanciaRecorrida = distanciaRecorrida;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public void setVelocidadPromEvento(String velocidadPromEvento) {
        this.velocidadPromEvento = velocidadPromEvento;
    }


}
