package com.example.stores

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StoreEntity")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name: String = "",
    val phone: String = "",
    val website: String = "",
    var photoUrl:String,
    var isFavorite: Boolean = false
)
