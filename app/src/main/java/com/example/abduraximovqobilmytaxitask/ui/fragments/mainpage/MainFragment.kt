package com.example.abduraximovqobilmytaxitask.ui.fragments.mainpage

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.abduraximovqobilmytaxitask.R
import com.example.abduraximovqobilmytaxitask.core.cache.AppCache
import com.example.abduraximovqobilmytaxitask.core.entity.LocationEntity
import com.example.abduraximovqobilmytaxitask.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
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
    private lateinit var lastLocation: Location
    private val viewModel: LocationVM by viewModels()
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var locationManager: LocationManager
    private lateinit var imageViewLanguage: ImageView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        binding.locationBtn.setOnClickListener {
            getUserLocation()
        }
        binding.menuBtn.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.mapView.getMapAsync {
            it.setMinZoomPreference(9.0)
            it.setMaxZoomPreference(18.0)
            it.uiSettings.isLogoEnabled = false
            it.uiSettings.isCompassEnabled = false
            it.uiSettings.isAttributionEnabled = false
            if (AppCache.appCache!!.getTheme()) {
                it.setStyle(Style.DARK)
                imageViewLanguage.setImageResource(R.drawable.sun_icon)
            } else {
                it.setStyle(Style.LIGHT)
                imageViewLanguage.setImageResource(R.drawable.moon_icon)
            }
        }
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        menuSelecting()
        binding.navView.inflateHeaderView(R.layout.header_main_drawer).setOnClickListener {
            Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
        }
        decreaseOrIncreaseZoom()
        getUserLocation()
        imageViewLanguage = binding.navView.getHeaderView(0)
            .findViewById(R.id.language_button)
        changeTheme()
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

    private fun navigate(action: Int) {
        findNavController().navigate(action)
        binding.drawerLayout.closeDrawer(Gravity.LEFT)
    }

    private fun menuSelecting() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.trips -> {
                    navigate(R.id.action_mainFragment_to_tripsFragment)
                    true
                }
                R.id.payment -> {
                    true
                }
                R.id.favorite -> {
                    true
                }
                R.id.promo_code -> {
                    true
                }
                R.id.notification -> {
                    true
                }
                R.id.support -> {
                    true
                }
                R.id.settings -> {
                    true
                }
                else -> false
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

    private fun getUserLocation() {
        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener { location ->
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
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMp = mapboxMap
    }
}