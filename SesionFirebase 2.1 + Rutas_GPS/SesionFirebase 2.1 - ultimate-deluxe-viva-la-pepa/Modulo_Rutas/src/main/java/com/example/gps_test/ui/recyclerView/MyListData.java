package com.example.gps_test.ui.recyclerView;

public class MyListData {
    private String description;
    private String description2;
    private int imgId;
    public MyListData(String description, String description2, int imgId) {
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
    public int getImgId() {
        return imgId;
    }
    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
