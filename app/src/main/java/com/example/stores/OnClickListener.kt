package com.example.stores

interface OnClickListener {
    fun onClick(store:StoreEntity)
    fun onFavoriteStore(store: StoreEntity)
    fun onDeleteStore(store: StoreEntity)
}