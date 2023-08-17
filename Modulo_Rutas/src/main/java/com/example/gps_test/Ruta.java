package com.example.gps_test;

import com.example.gps_test.ui.map.Curvaturas;
import com.example.gps_test.ui.recyclerView.MyListData;
import com.google.firebase.database.IgnoreExtraProperties;
import com.tomtom.sdk.location.GeoPoint;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Ruta {
    public String routeName;
    public int type;
    public List<GeoPoint> routePoints = new ArrayList<GeoPoint>();
    public Double lenght;
    public int curvesAmount;
    public String startLocation;
    public String finishLocation;
    public List<Curvaturas> rearangedRoutePoints = new ArrayList<Curvaturas>();
    public List<MyListData> routeResumeData = new ArrayList<MyListData>();


    public Ruta() {
        // Default constructor required for calls to DataSnapshot.getValue(Ruta.class)
    }

    public Ruta(String routeName, int type, List<GeoPoint> routePoints, Double lenght, int curvesAmount, String startLocation, String finishLocation, List<Curvaturas> rearangedRoutePoints, List<MyListData> routeResumeData) {
        this.routeName = routeName;
        this.type = type;
        this.routePoints = routePoints;
        this.lenght = lenght;
        this.curvesAmount = curvesAmount;
        this.startLocation = startLocation;
        this.finishLocation = finishLocation;
        this.rearangedRoutePoints = rearangedRoutePoints;
        this.routeResumeData = routeResumeData;
    }
}
