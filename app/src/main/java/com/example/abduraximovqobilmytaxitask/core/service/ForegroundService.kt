package com.example.abduraximovqobilmytaxitask.core.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import com.example.abduraximovqobilmytaxitask.core.app.App
import com.example.abduraximovqobilmytaxitask.ui.fragments.mainpage.MainFragment
import com.google.android.gms.location.*
import com.mapbox.mapboxsdk.geometry.LatLng


class ForegroundService : Service(), LocationListener {
    private val binder = LocalBinder(this)
    private lateinit var locationManager: LocationManager
    private var lastLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 3000 // Update location every 5 seconds
            fastestInterval = 2000 // Get the location as fast as possible
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Use high accuracy mode
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.let { result ->
                    val location = result.lastLocation
                    markLocation(location!!)
                    sendLocationBroadcast(location)
                }
            }
        }
    }


    companion object {
        const val ACTION_LOCATION_UPDATE = "FOREGROUND_SERVICE_TO_MAIN_FRAGMENT"
        const val EXTRA_LOCATION = "com.example.abduraximovqobilmytaxitask.ui.fragments.mainpage"
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.myLooper()
        )
    }


    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback!!)
        }
    }

    private fun sendLocationBroadcast(location: Location) {
        val intent = Intent(App.instance, MainFragment::class.java).apply {
            putExtra(EXTRA_LOCATION, location)
        }
        intent.action = ACTION_LOCATION_UPDATE
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        try {
            pendingIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }
    }


    override fun onLocationChanged(location: Location) {
        if (lastLocation == null || lastLocation!!.distanceTo(location) > 5) {
            markLocation(location)
            lastLocation = location
        }
    }

    private fun markLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val intent = Intent("location-update")
        intent.putExtra("lat", location.latitude)
        intent.putExtra("lng", location.longitude)
        sendBroadcast(intent)
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
//        Toast.makeText(App.instance!!, "$provider", Toast.LENGTH_SHORT).show()
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    class LocalBinder(private val service: ForegroundService) : Binder() {

        fun getService(): ForegroundService {
            return service
        }

    }

}