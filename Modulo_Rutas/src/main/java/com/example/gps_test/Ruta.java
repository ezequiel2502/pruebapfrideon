package com.example.gps_test;

import android.util.Pair;

import com.example.gps_test.ui.map.Curvaturas;
import com.example.gps_test.ui.map.TupleDouble;
import com.example.gps_test.ui.recyclerView.MyListData;
import com.google.firebase.database.IgnoreExtraProperties;
import com.tomtom.sdk.location.GeoPoint;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Ruta {
    public String author;
    public String routeName;
    public int type;
    public List<TupleDouble> routePoints = new ArrayList<TupleDouble>();
    public Double lenght;
    public int curvesAmount;
    public String startLocation;
    public String finishLocation;
    public String imgLocation;
    public List<MyListData> routeResumeData = new ArrayList<MyListData>();
    public List<Curvaturas> routePointsNavigation = new ArrayList<Curvaturas>();


    public Ruta() {
        // Default constructor required for calls to DataSnapshot.getValue(Ruta.class)
    }

    public Ruta(String author, String routeName, int type, List<TupleDouble> routePoints, Double lenght, int curvesAmount, String startLocation, String finishLocation, String imgLocation, List<MyListData> routeResumeData,List<Curvaturas> routePointsNavigation) {
        this.author = author;
        this.routeName = routeName;
        this.type = type;
        this.routePoints = routePoints;
        this.lenght = lenght;
        this.curvesAmount = curvesAmount;
        this.startLocation = startLocation;
        this.finishLocation = finishLocation;
        this.imgLocation = imgLocation;
        this.routeResumeData = routeResumeData;
        this.routePointsNavigation = routePointsNavigation;
    }
}
