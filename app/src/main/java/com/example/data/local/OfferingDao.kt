package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.OfferingRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface OfferingDao {
    @Query("SELECT * FROM offering_records ORDER BY timestamp DESC")
    fun getAllOfferings(): Flow<List<OfferingRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffering(offering: OfferingRecord)
}
