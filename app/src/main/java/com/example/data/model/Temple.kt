package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temples")
data class Temple(
    @PrimaryKey val id: String,
    val name: String,
    val deity: String,
    val city: String,
    val state: String,
    val latitude: Double,
    val longitude: Double,
    val verifiedAccessCode: String,
    val description: String,
    val timings: String,
    val isPermitted: Boolean = true,
    val activeSevaksCount: Int = 1,
    val sanctumFocusAreas: String = "Garbhagriha Sanctum, Aarti View, Shivalinga/Vigraha, Parikrama Corridor"
)
