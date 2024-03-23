package com.example.sesionconfirebase;

public class ModelNotificacion {

    private String titulo;
    private String detalle;
    private String tipoNotificacion;
    private String idEvento;
    private String idOrganizador;
    private String postulanteId;
    private String nombreEvento;

    public String getTokenPostulante() {
        return TokenPostulante;
    }

    public void setTokenPostulante(String tokenPostulante) {
        TokenPostulante = tokenPostulante;
    }

    private String TokenPostulante;
    private String idNotificacion;

    public ModelNotificacion() {
    }

    public ModelNotificacion(String titulo,String detalle,String tipoNotificacion,String idEvento,String idOrganizador,String postulanteId,String nombreEvento,String TokenPostulante) {
        this.nombreEvento = nombreEvento;
        this.titulo = detalle;
        this.tipoNotificacion = tipoNotificacion;
        this.idEvento = idEvento;
        this.postulanteId = postulanteId;
        this.nombreEvento = nombreEvento;
        this.idOrganizador = idOrganizador;
        this.TokenPostulante = TokenPostulante;
    }

    public String getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(String idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getTipoNotificacion() {
        return tipoNotificacion;
    }

    public String getIdEvento() {
        return idEvento;
    }

    public String getPostulanteId() {
        return postulanteId;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public String getIdOrganizador() {
        return idOrganizador;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public void setTipoNotificacion(String tipoNotificacion) {
        this.tipoNotificacion = tipoNotificacion;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public void setPostulanteId(String postulanteId) {
        this.postulanteId = postulanteId;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public void setIdOrganizador(String idOrganizador) {
        this.idOrganizador = idOrganizador;
    }
}
