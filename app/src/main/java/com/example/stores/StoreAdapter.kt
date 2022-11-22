package com.example.stores

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.databinding.ItemViewBinding

class StoreAdapter {
    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val bindin = ItemViewBinding.bind(view)

    }
}