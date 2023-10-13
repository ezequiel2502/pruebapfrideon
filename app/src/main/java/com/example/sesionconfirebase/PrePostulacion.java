package com.example.sesionconfirebase;

public class PrePostulacion {

    private String tokenFcmPostulante;
    private Boolean aceptado;

    private  String userId;

    private String eventoId;

    public PrePostulacion() {
    }

    public PrePostulacion(String tokenFcmPostulante, Boolean aceptado, String userId, String eventoId) {
        this.tokenFcmPostulante = tokenFcmPostulante;
        this.aceptado = aceptado;
        this.userId = userId;
        this.eventoId = eventoId;
    }


    //Getters
    public String getUserId() {
        return userId;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getTokenFcmPostulante() {
        return tokenFcmPostulante;
    }

    public Boolean getAceptado() {
        return aceptado;
    }

    //Setters


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
    }

    public void setTokenFcmPostulante(String tokenFcmPostulante) {
        this.tokenFcmPostulante = tokenFcmPostulante;
    }

    public void setAceptado(Boolean aceptado) {
        this.aceptado = aceptado;
    }
}
