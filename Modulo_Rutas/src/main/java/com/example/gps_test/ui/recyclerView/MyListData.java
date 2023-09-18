package com.example.gps_test.ui.recyclerView;

import java.io.Serializable;

public class MyListData implements Serializable {
    private String description;
    private String description2;
    private String imgId;

    public MyListData() {
        // Default constructor required for calls to DataSnapshot.getValue(Ruta.class)
    }
    public MyListData(String description, String description2, String imgId) {
        this.description = description;
        this.description2 = description2;
        this.imgId = imgId;
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
}
