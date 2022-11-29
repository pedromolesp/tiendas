package com.example.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.databinding.ItemViewBinding

class StoreAdapter(private var stores: MutableList<StoreEntity>, private var listener: OnClickListener) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemViewBinding.bind(view)
        fun setListener(store: StoreEntity) {
            binding.root.setOnClickListener { listener.onClick(store) }
            binding.cbFavorite.setOnClickListener{
                listener.onFavoriteStore(store)
            }
        }
    }

    private lateinit var nContext: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        nContext = parent.context

        val view = LayoutInflater.from(nContext).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = stores.get(position)
        with(holder) {
            setListener(store)
            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isFavorite

        }
    }

    override fun getItemCount(): Int = stores.size
    fun add(store: StoreEntity) {
        stores.add(store)
        notifyDataSetChanged()
    }

    fun setStores(stores: MutableList<StoreEntity>) {
        this.stores = stores
        notifyDataSetChanged()

    }

    fun update(store: StoreEntity) {
        val index = stores.indexOf(store)
        if(index != -1){
            stores.set(index,store)
            notifyItemChanged(index)
        }
    }
}