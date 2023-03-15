package com.example.abduraximovqobilmytaxitask.ui.fragments.mainpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.abduraximovqobilmytaxitask.core.app.App
import com.example.abduraximovqobilmytaxitask.core.database.LocationDatabase
import com.example.abduraximovqobilmytaxitask.core.entity.LocationEntity
import com.example.abduraximovqobilmytaxitask.core.repo.LocationRepository
import kotlinx.coroutines.launch

class LocationVM : ViewModel() {
    private var locationRepository: LocationRepository
    fun setLocation(locationEntity: LocationEntity) {
        viewModelScope.launch {
            locationRepository.setLocation(locationEntity = locationEntity)
        }
    }

    init {
        val dao = LocationDatabase.getDatabase(App.instance!!).dao()
        locationRepository = LocationRepository(dao)
    }
}