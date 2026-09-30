package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "darshan_requests")
data class DarshanRequest(
    @PrimaryKey val id: String,
    val devoteeName: String,
    val devoteePhone: String,
    val templeId: String,
    val templeName: String,
    val sevakId: String,
    val sevakName: String,
    val sankalpaPrayer: String,
    val familyGotra: String = "",
    val focusPreference: String = "Garbhagriha Sanctum",
    val status: String = "PENDING", // PENDING, ACCEPTED, IN_CALL, COMPLETED, CANCELLED
    val createdAt: Long = System.currentTimeMillis(),
    val channelId: String = "webrtc_room_${System.currentTimeMillis()}",
    val offeringAmount: Int = 0
)
