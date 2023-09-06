package com.example.sesionconfirebase;

public class ModelComentario {
    private String publisherId,comment,imagen_perfil,publisherName,commentId,eventoId;

    public ModelComentario(String publisherId, String comment, String imagen_perfil,
                           String publisherName, String commentId, String eventoId) {
        this.publisherId = publisherId;
        this.comment = comment;
        this.imagen_perfil = imagen_perfil;
        this.publisherName = publisherName;
        this.commentId = commentId;
        this.eventoId = eventoId;
    }

    public String getEventoId() {
        return eventoId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public String getImagen_perfil() {
        return imagen_perfil;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public String getComment() {
        return comment;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setImagen_perfil(String imagen_perfil) {
        this.imagen_perfil = imagen_perfil;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setEventoId(String eventoId) {
        this.eventoId = eventoId;
    }
}
