package com.example.sesionconfirebase;

public class ModelRespuestaComentario {

    private String publisherId,comment,imagen_perfil,publisherName,commentId,eventoId,parentCommentId,tipo;

    public ModelRespuestaComentario() {
    }


    public ModelRespuestaComentario(String publisherId, String comment, String imagen_perfil, String publisherName, String commentId, String eventoId, String parentCommentId, String tipo) {
        this.publisherId = publisherId;
        this.comment = comment;
        this.imagen_perfil = imagen_perfil;
        this.publisherName = publisherName;
        this.commentId = commentId;
        this.eventoId = eventoId;
        this.parentCommentId = parentCommentId;
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImagen_perfil() {
        return imagen_perfil;
    }

    public void setImagen_perfil(String imagen_perfil) {
        this.imagen_perfil = imagen_perfil;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getEventoId() {
        return eventoId;
    }

    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
    }

    public String getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}
