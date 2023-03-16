package com.example.abduraximovqobilmytaxitask.ui.fragments.mainpage

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.*
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.abduraximovqobilmytaxitask.R
import com.example.abduraximovqobilmytaxitask.core.cache.AppCache
import com.example.abduraximovqobilmytaxitask.core.entity.LocationEntity
import com.example.abduraximovqobilmytaxitask.core.service.ForegroundService
import com.example.abduraximovqobilmytaxitask.databinding.FragmentMainBinding
import com.google.android.gms.location.*
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.utils.BitmapUtils

class MainFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMainBinding
    private lateinit var mapboxMp: MapboxMap
    private var marker: Marker? = null
    private val viewModel: LocationVM by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var imageViewLanguage: ImageView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var headerView: View
    private var serviceConnection: ServiceConnection? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))

        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        binding.locationBtn.setOnClickListener {
            getUserLocation()
        }
//        bindService()
        getUserLocation()
        drawerHeaderListener()
        requestPermission()
        decreaseOrIncreaseZoom()
        changeTheme()

    }

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action == ForegroundService.ACTION_LOCATION_UPDATE) {
                val location =
                    intent.getParcelableExtra<Location>(ForegroundService.EXTRA_LOCATION)
                Toast.makeText(requireContext(), "${location!!.latitude}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                getUserLocation()
            }
            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                getUserLocation()
            }
            permissions.getOrDefault(ACCESS_BACKGROUND_LOCATION, false) -> {
                getUserLocation()
            }
            else -> {
            }
        }
    }


    private fun requestPermission() {
        if (!PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            locationPermissionRequest.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION,
                )
            )
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(64.000, 41.000)).zoom(15.0).build()
            mapboxMp.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                1500,
                object : MapboxMap.CancelableCallback {
                    override fun onCancel() {

                    }

                    override fun onFinish() {

                    }
                })
        }

    }

    private fun drawerHeaderListener() {
        headerView = binding.navView.inflateHeaderView(R.layout.header_main_drawer)
        binding.menuBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        toggle = ActionBarDrawerToggle(
            requireActivity(), binding.drawerLayout, R.string.open, R.string.close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        imageViewLanguage = headerView.findViewById(R.id.language_button)
    }

    private fun changeTheme() {
        imageViewLanguage.setOnClickListener {
            if (AppCache.appCache!!.getTheme()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                AppCache.appCache!!.setTheme(false)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                AppCache.appCache!!.setTheme(true)
            }
        }
    }

    private fun decreaseOrIncreaseZoom() {
        binding.decreaseZoomBtn.setOnClickListener {
            binding.mapView.getMapAsync {
                it.animateCamera(CameraUpdateFactory.zoomOut())
            }

        }
        binding.increaseZoomBtn.setOnClickListener {
            binding.mapView.getMapAsync {
                it.animateCamera(CameraUpdateFactory.zoomIn())
            }
        }
    }

    private fun bindService() {
        val intent = Intent(requireActivity(), ForegroundService::class.java)
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as ForegroundService.LocalBinder
                val foregroundService = binder.getService()
                foregroundService.startLocationUpdates()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                serviceConnection = null
            }
        }
        requireActivity().bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val markerOptions = MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    .icon(
                        BitmapUtils.getBitmapFromDrawable(
                            resources.getDrawable(R.drawable.icon_taxi)
                        )?.let { IconFactory.getInstance(requireContext()).fromBitmap(it) })
                viewModel.setLocation(
                    LocationEntity(
                        longitude = location.longitude,
                        latitude = location.latitude
                    )
                )
                binding.mapView.getMapAsync {
                    it.addMarker(markerOptions)
                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(location.latitude, location.longitude))
                        .zoom(15.0)
                        .build()
                    it.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        1500,
                        object : MapboxMap.CancelableCallback {
                            override fun onCancel() {

                            }

                            override fun onFinish() {

                            }

                        }
                    )
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        context?.let {
            val intentFilter = IntentFilter(ForegroundService.ACTION_LOCATION_UPDATE)
            it.registerReceiver(locationReceiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onStop()

        context?.let {
            it.unregisterReceiver(locationReceiver)
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        val filter = IntentFilter("FOREGROUND_SERVICE_TO_MAIN_FRAGMENT")
        requireActivity().registerReceiver(locationReceiver, filter)
        super.onDestroy()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMp = mapboxMap
        binding.mapView.getMapAsync {
            it.setMinZoomPreference(9.0)
            it.setMaxZoomPreference(17.0)
            it.uiSettings.isLogoEnabled = false
            it.uiSettings.isCompassEnabled = false
            it.uiSettings.isAttributionEnabled = false
            if (AppCache.appCache!!.getTheme()) {
                it.setStyle(Style.DARK)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                imageViewLanguage.setImageResource(R.drawable.sun_icon)
            } else {
                it.setStyle(Style.LIGHT)
                imageViewLanguage.setImageResource(R.drawable.moon_icon)
            }
        }
    }
}