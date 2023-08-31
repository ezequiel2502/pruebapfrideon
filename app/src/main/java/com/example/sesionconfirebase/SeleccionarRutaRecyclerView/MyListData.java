package com.example.sesionconfirebase.SeleccionarRutaRecyclerView;

import com.example.gps_test.ui.map.TupleDouble;

import java.io.Serializable;
import java.util.List;

public class MyListData implements Serializable {
    private String routeName;
    private String databaseID;
    private List<TupleDouble> geometry;
    private int type;
    private Double lenght;
    private int curvesAmount;
    private String startLocation;
    private String finishLocation;
    private String imgId;



    public MyListData() {
        // Default constructor required for calls to DataSnapshot.getValue(Ruta.class)
    }

    public MyListData(String routeName, String databaseID, List<TupleDouble> geometry, int type, Double lenght, int curvesAmount, String startLocation, String finishLocation, String imgId) {
        this.setRouteName(routeName);
        this.setDatabaseID(databaseID);
        this.setGeometry(geometry);
        this.setType(type);
        this.setLenght(lenght);
        this.setCurvesAmount(curvesAmount);
        this.setStartLocation(startLocation);
        this.setFinishLocation(finishLocation);
        this.setImgId(imgId);
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getLenght() {
        return lenght;
    }

    public void setLenght(Double lenght) {
        this.lenght = lenght;
    }

    public int getCurvesAmount() {
        return curvesAmount;
    }

    public void setCurvesAmount(int curvesAmount) {
        this.curvesAmount = curvesAmount;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getFinishLocation() {
        return finishLocation;
    }

    public void setFinishLocation(String finishLocation) {
        this.finishLocation = finishLocation;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

    public List<TupleDouble> getGeometry() {
        return geometry;
    }

    public void setGeometry(List<TupleDouble> geometry) {
        this.geometry = geometry;
    }
}
