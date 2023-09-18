package com.example.sesionconfirebase;

import java.util.ArrayList;
import java.util.List;

public class ModelComentario {
    private String publisherId,comment,imagen_perfil,publisherName,commentId,eventoId,tipo;
    private ModelRespuestaComentario respuesta;

    public ModelComentario(String publisherId, String comment, String imagen_perfil, String publisherName, String commentId, String eventoId, String tipo, ModelRespuestaComentario respuesta) {
        this.publisherId = publisherId;
        this.comment = comment;
        this.imagen_perfil = imagen_perfil;
        this.publisherName = publisherName;
        this.commentId = commentId;
        this.eventoId = eventoId;
        this.tipo = tipo;
        this.respuesta = respuesta;
    }

    public ModelComentario(String publisherId, String comment, String imagen_perfil, String publisherName, String commentId, String eventoId, String tipo) {
        this.publisherId = publisherId;
        this.comment = comment;
        this.imagen_perfil = imagen_perfil;
        this.publisherName = publisherName;
        this.commentId = commentId;
        this.eventoId = eventoId;
        this.tipo = tipo;
    }


    public ModelRespuestaComentario getRespuesta() {
        return respuesta;
    }

    public String getTipo() {
        return tipo;
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

       public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setRespuesta(ModelRespuestaComentario respuesta) {
        this.respuesta = respuesta;
    }
}
