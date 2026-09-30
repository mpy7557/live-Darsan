package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DarshanRequest
import com.example.data.model.OfferingRecord
import com.example.data.model.Sevak
import com.example.data.model.Temple

@Database(
    entities = [
        Temple::class,
        Sevak::class,
        DarshanRequest::class,
        OfferingRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DarshanDatabase : RoomDatabase() {
    abstract fun templeDao(): TempleDao
    abstract fun sevakDao(): SevakDao
    abstract fun darshanRequestDao(): DarshanRequestDao
    abstract fun offeringDao(): OfferingDao

    companion object {
        @Volatile
        private var INSTANCE: DarshanDatabase? = null

        fun getDatabase(context: Context): DarshanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DarshanDatabase::class.java,
                    "darshan_live_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
