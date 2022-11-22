package com.example.stores

data class Store(
    val id: Long = 0,
    val name: String = "",
    val phone: String = "",
    val website: String = "",
    val isFavorite: Boolean = false
)
