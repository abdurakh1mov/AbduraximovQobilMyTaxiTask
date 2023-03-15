package com.example.abduraximovqobilmytaxitask.core.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.abduraximovqobilmytaxitask.core.entity.LocationEntity

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertExample(locationEntity: LocationEntity)
}