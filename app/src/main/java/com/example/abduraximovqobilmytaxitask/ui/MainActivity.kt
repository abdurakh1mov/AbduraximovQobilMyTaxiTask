package com.example.abduraximovqobilmytaxitask.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.abduraximovqobilmytaxitask.databinding.ActivityMainBinding

//import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        if (AppCache.appCache!!.getTheme()){
//            Toast.makeText(this, "Dark", Toast.LENGTH_SHORT).show()
//           setTheme(R.style.Theme_AbduraximovQobilMyTaxiTaskDark)
//        }else{
//            Toast.makeText(this, "Light", Toast.LENGTH_SHORT).show()
//            setTheme(R.style.Theme_AbduraximovQobilMyTaxiTaskLight)
//        }
    }
}