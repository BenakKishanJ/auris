package com.example.auris.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val fileUri: String,
    val lastReadPage: Int = 0,
    val addedDate: Long = System.currentTimeMillis() // To show newest books first
)

