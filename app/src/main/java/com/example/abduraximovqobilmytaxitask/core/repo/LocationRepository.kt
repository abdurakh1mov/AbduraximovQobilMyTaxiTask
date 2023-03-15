package com.example.abduraximovqobilmytaxitask.core.repo

import com.example.abduraximovqobilmytaxitask.core.dao.LocationDao
import com.example.abduraximovqobilmytaxitask.core.entity.LocationEntity

class LocationRepository(private var locationDao: LocationDao) {
    suspend fun setLocation(locationEntity: LocationEntity) {
        locationDao.insertExample(locationEntity)
    }
}