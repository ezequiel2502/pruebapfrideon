package com.example.sesionconfirebase;

public class ModelCalificacion {
    private String userId;
    private float rating;

    public ModelCalificacion() {
        // Constructor vacío requerido por Firebase
    }

    public ModelCalificacion(String userId, float rating) {
        this.userId = userId;
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}

