package com.example.stores

interface OnClickListener {
    fun onClick(id:Long)
    fun onFavoriteStore(store: StoreEntity)
    fun onDeleteStore(store: StoreEntity)
}