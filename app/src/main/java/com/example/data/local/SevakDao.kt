package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Sevak
import kotlinx.coroutines.flow.Flow

@Dao
interface SevakDao {
    @Query("SELECT * FROM sevaks")
    fun getAllSevaks(): Flow<List<Sevak>>

    @Query("SELECT * FROM sevaks WHERE templeId = :templeId")
    fun getSevaksForTemple(templeId: String): Flow<List<Sevak>>

    @Query("SELECT * FROM sevaks WHERE id = :id")
    fun getSevakById(id: String): Flow<Sevak?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSevak(sevak: Sevak)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSevaks(sevaks: List<Sevak>)

    @Query("UPDATE sevaks SET isOnline = :isOnline, isInsideGeofence = :isInside, distanceMeters = :dist WHERE id = :id")
    suspend fun updateSevakPresence(id: String, isOnline: Boolean, isInside: Boolean, dist: Float)

    @Query("UPDATE sevaks SET isVerified = :isVerified WHERE id = :id")
    suspend fun updateVerification(id: String, isVerified: Boolean)
}
