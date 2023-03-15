package com.example.abduraximovqobilmytaxitask.ui.fragments.mainpage

import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

class MyLocationEngineCallback(private val mapboxMap: MapboxMap) :
    LocationEngineCallback<LocationEngineResult> {
    override fun onSuccess(result: LocationEngineResult?) {
        result?.lastLocation?.let { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            mapboxMap.locationComponent.forceLocationUpdate(location)

        }
    }

    override fun onFailure(p0: Exception) {

    }
}