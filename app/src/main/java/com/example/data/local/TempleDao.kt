package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Temple
import kotlinx.coroutines.flow.Flow

@Dao
interface TempleDao {
    @Query("SELECT * FROM temples")
    fun getAllTemples(): Flow<List<Temple>>

    @Query("SELECT * FROM temples WHERE id = :id")
    fun getTempleById(id: String): Flow<Temple?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemples(temples: List<Temple>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemple(temple: Temple)
}
