package com.example.stores

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

       /* mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.etName.text.toString().trim())
            Thread {
                StoreApplication.database.storeDao().addStore(store)
            }.start()
            mAdapter.add(store)
        }*/
        mBinding.fabAdd.setOnClickListener {
            launchEditFragment()
        }

        setupRecyclerView()
    }

    private fun launchEditFragment() {
        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
//        mBinding.fabAdd.hide()
        hideFab()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        getStore()
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }

    }

    private fun getStore() {
        doAsync {
            val stores = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                mAdapter.setStores(stores)
            }
        }
    }

    /*
     * OnClickListener
     */
    override fun onClick(store: StoreEntity) {

    }

    override fun onFavoriteStore(store: StoreEntity) {
        store.isFavorite = !store.isFavorite
        doAsync {
            StoreApplication.database.storeDao().updateStore(store)
            uiThread {
                mAdapter.update(store)
            }
        }
    }

    override fun onDeleteStore(store: StoreEntity) {
        doAsync {

            StoreApplication.database.storeDao().deleteStore(store)
            uiThread {
                mAdapter.delete(store)
            }
        }
    }
/*
* MainAux
* */
    override fun hideFab(isVisible: Boolean) {
        if(isVisible) mBinding.fabAdd.show( ) else mBinding.fabAdd.hide()
    }
}