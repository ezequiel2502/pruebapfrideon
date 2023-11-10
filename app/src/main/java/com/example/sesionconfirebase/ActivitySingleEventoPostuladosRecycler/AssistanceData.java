package com.example.sesionconfirebase.ActivitySingleEventoPostuladosRecycler;

import java.io.Serializable;

public class AssistanceData implements Serializable {
    private String description;
    private String description2;
    private String imgId;
    private String idUsuario;

    public AssistanceData() {
        // Default constructor required for calls to DataSnapshot.getValue(Ruta.class)
    }
    public AssistanceData(String description, String description2, String imgId, String idUsuario) {
        this.description = description;
        this.description2 = description2;
        this.imgId = imgId;
        this.idUsuario = idUsuario;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription2() {
        return description2;
    }
    public void setDescription2(String description2) {
        this.description2 = description2;
    }
    public String getImgId() {
        return imgId;
    }
    public void setImgId(String imgId) {
        this.imgId = imgId;
    }
    public String getIdUsuario() {
        return idUsuario;
    }

}
