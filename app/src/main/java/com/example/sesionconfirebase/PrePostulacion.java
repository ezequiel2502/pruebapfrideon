package com.example.sesionconfirebase;

public class PrePostulacion {

    private String tokenFcmPostulante;
    private Boolean aceptado;

    public PrePostulacion() {
    }

    public PrePostulacion(String tokenFcmPostulante, Boolean aceptado) {
        this.tokenFcmPostulante = tokenFcmPostulante;
        this.aceptado = aceptado;
    }

    public String getTokenFcmPostulante() {
        return tokenFcmPostulante;
    }

    public Boolean getAceptado() {
        return aceptado;
    }

    public void setTokenFcmPostulante(String tokenFcmPostulante) {
        this.tokenFcmPostulante = tokenFcmPostulante;
    }

    public void setAceptado(Boolean aceptado) {
        this.aceptado = aceptado;
    }
}
