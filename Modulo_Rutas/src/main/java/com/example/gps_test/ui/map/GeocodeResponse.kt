package com.example.gps_test.ui.map

import android.content.Context
import com.example.gps_test.BuildConfig
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.search.common.error.SearchFailure
import com.tomtom.sdk.search.reversegeocoder.ReverseGeocoder
import com.tomtom.sdk.search.reversegeocoder.ReverseGeocoderCallback
import com.tomtom.sdk.search.reversegeocoder.ReverseGeocoderOptions
import com.tomtom.sdk.search.reversegeocoder.ReverseGeocoderResponse
import com.tomtom.sdk.search.reversegeocoder.online.OnlineReverseGeocoder


class GeocodeResponse {

    fun setTest(positions: GeoPoint, c: Context) : ReverseGeocoderResponse? {
        var result1: ReverseGeocoderResponse? = null

        val reverseGeocoder: ReverseGeocoder = OnlineReverseGeocoder.create(c, "EkmKRJIDeBAZcYixlW5NBNIGoNOlyB2Y")

        val reverseGeocoderOptions = ReverseGeocoderOptions(
                position = GeoPoint(-31.421647, -64.183359),
        )

        reverseGeocoder.reverseGeocode(reverseGeocoderOptions, object : ReverseGeocoderCallback {

            override fun onSuccess(result: ReverseGeocoderResponse) {

               result1 = result

            }


            override fun onFailure(failure: SearchFailure) {

                println(failure)

            }

        })


        return result1
    }




    fun setOptions(positions: GeoPoint) : ReverseGeocoderOptions {
        val reverseGeocoderOptions = ReverseGeocoderOptions(
                position = positions,
                )
        return reverseGeocoderOptions
    }
}