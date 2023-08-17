package com.example.gps_test.ui.map

import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.image.Image
import com.tomtom.sdk.map.display.marker.MarkerOptions

class Marker_Variables {


    fun setMarkerOptions(position: GeoPoint, image: Image, type: String) : MarkerOptions {
        val markerOptions = MarkerOptions(

                coordinate = position,

                pinImage = image,

                tag = type

        )
        return markerOptions
    }
}