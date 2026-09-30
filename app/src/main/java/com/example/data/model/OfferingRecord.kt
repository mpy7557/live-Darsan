package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offering_records")
data class OfferingRecord(
    @PrimaryKey val id: String,
    val templeName: String,
    val sevakName: String,
    val devoteeName: String,
    val amount: Int,
    val category: String, // "Temple Trust Hundi", "Flowers & Prasad Seva", "Sevak Dakshina"
    val paymentId: String,
    val orderId: String,
    val status: String = "SUCCESS",
    val timestamp: Long = System.currentTimeMillis(),
    val taxExempt80G: String = "80G-DARSHAN-${System.currentTimeMillis() % 1000000}"
)
