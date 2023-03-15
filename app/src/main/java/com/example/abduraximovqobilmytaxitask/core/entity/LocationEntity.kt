package com.example.abduraximovqobilmytaxitask.core.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
class LocationEntity(
    @ColumnInfo(name = "longitude") var longitude: Double?,
    @ColumnInfo(name = "latitude") var latitude: Double?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
