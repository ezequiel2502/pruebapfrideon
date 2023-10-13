package com.example.gps_test.ui.map

import com.tomtom.quantity.Distance

import com.tomtom.sdk.location.android.AndroidLocationProviderConfig
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import kotlin.time.Duration.Companion.milliseconds


 class Location_Variables {
    val androidLocationProviderConfig = AndroidLocationProviderConfig(

            minTimeInterval = 150L.milliseconds,

            minDistance = Distance.meters(2.5)

    )

     val LocationMarker = LocationMarkerOptions(type=LocationMarkerOptions.Type.Pointer)


}