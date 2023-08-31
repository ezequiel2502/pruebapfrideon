package com.example.gps_test.ui.map;

import java.io.Serializable;

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
}
