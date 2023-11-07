package com.example.gps_test.ui.map

import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.camera.CameraTrackingMode

class Map_Camera_Settings {

    fun setTracking(map: TomTomMap) {
        map.cameraTrackingMode = CameraTrackingMode.FollowDirection
    }

    fun disableTracking(map: TomTomMap) {
        map.cameraTrackingMode = CameraTrackingMode.None
        map.moveCamera(CameraOptions(map.cameraPosition.position, 15.0, 0.0, 0.0))
    }
}