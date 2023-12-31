package com.example.gps_test.ui.map;

import com.tomtom.sdk.location.GeoPoint;

import java.util.List;

public class Curvaturas {

    private final double curvatureRadians;
    private final double curvatureAngle;
    private final List<GeoPoint> segmentPoints;
    private final double turnDirection;
    private final String curvatureType;
    private final double curvatureLength;

    public Curvaturas(double curvatureRadians, double curvatureAngle, List<GeoPoint> segmentPoints, double turnDirection, String curvatureType, double curvatureLength) {
        this.curvatureRadians = curvatureRadians;
        this.curvatureAngle = curvatureAngle;
        this.segmentPoints = segmentPoints;
        this.turnDirection = turnDirection;
        this.curvatureType = curvatureType;
        this.curvatureLength = curvatureLength;
    }


    public double getCurvatureRadians() {
        return curvatureRadians;
    }

    public double getCurvatureAngle() {
        return curvatureAngle;
    }

    public List<GeoPoint> getSegmentPoints() {
        return segmentPoints;
    }

    public String getCurvatureType() {
        return curvatureType;
    }

    public double getCurvatureLength() {
        return curvatureLength;
    }

    public double getTurnDirection() {
        return turnDirection;
    }
}
