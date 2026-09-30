package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sevaks")
data class Sevak(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val templeId: String,
    val templeName: String,
    val isVerified: Boolean = true,
    val bio: String,
    val photoRes: String = "img_sevak_portrait_1790748141779",
    val darshansConducted: Int = 142,
    val rating: Double = 4.9,
    val languages: String = "Hindi, English, Sanskrit",
    val isOnline: Boolean = true,
    val isInsideGeofence: Boolean = true,
    val distanceMeters: Float = 75f,
    val sevaBadge: String = "Certified Temple Sevak",
    val joinedDate: String = "Seva since Jan 2024"
)
