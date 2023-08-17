package com.example.gps_test.ui.map

import com.tomtom.quantity.Distance
import com.tomtom.sdk.common.measures.UnitSystem
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.search.SearchOptions
import com.tomtom.sdk.search.online.OnlineSearch
import com.tomtom.sdk.search.ui.SearchFragment
import com.tomtom.sdk.search.ui.model.SearchApiParameters
import com.tomtom.sdk.search.ui.model.SearchProperties
import java.util.Locale
import com.example.gps_test.BuildConfig

class Search_Variables {



    fun setSearchProperties(position: GeoPoint, language: Locale, results_limit: Int) : SearchProperties{

        val searchApiParameters = SearchApiParameters(

                limit = results_limit,

                position = position,

                language = language

        )

        val searchProperties = SearchProperties(

                searchApiKey = BuildConfig.CREDENTIALS_KEY,

                searchApiParameters = searchApiParameters,

                commands = listOf("TomTom"),

                units = UnitSystem.Metric

        )

        return searchProperties
    }



    fun setCamera(position: GeoPoint, zoom: Double, tilt: Double, rotation: Double) : CameraOptions{
        val camera = CameraOptions(

                position = position,

                zoom = zoom,

                tilt = tilt,

                rotation = rotation
        )
        return camera
    }







}