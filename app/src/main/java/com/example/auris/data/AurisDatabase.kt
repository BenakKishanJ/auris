package com.example.auris.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
abstract class AurisDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AurisDatabase? = null

        fun getDatabase(context: Context): AurisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AurisDatabase::class.java,
                    "auris_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}