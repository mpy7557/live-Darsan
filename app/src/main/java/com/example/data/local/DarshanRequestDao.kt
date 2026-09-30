package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DarshanRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface DarshanRequestDao {
    @Query("SELECT * FROM darshan_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<DarshanRequest>>

    @Query("SELECT * FROM darshan_requests WHERE sevakId = :sevakId ORDER BY createdAt DESC")
    fun getRequestsForSevak(sevakId: String): Flow<List<DarshanRequest>>

    @Query("SELECT * FROM darshan_requests WHERE id = :id")
    fun getRequestById(id: String): Flow<DarshanRequest?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: DarshanRequest)

    @Query("UPDATE darshan_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: String, status: String)

    @Query("DELETE FROM darshan_requests WHERE id = :id")
    suspend fun deleteRequest(id: String)
}
