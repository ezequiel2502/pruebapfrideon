package com.example.gps_test.ui.map;

import com.tomtom.sdk.location.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TupleDouble implements Serializable {

    private double latitude;
    private double longitude;

    public TupleDouble()
    {

    }

    public TupleDouble(double latitude, double longitude)
    {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public GeoPoint tupleToGeopoint(TupleDouble tuple)
    {
       return new GeoPoint(tuple.getLatitude(), tuple.getLongitude());
    }

    public List<GeoPoint> listTupleTolistGeo(List<TupleDouble> ev) {

        List<GeoPoint> a = new ArrayList<>();

        for (int i = 0; i < ev.size(); i++) {
            a.add(new GeoPoint(ev.get(i).getLatitude(), ev.get(i).getLongitude()));
        }
        return a;
    }
}
