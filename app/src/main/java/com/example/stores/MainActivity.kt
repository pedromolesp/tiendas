package com.example.stores

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()
        if (args != null) {
            fragment.arguments = args
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
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
    override fun onClick(id: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), id)
        launchEditFragment(args)
    }

    override fun onFavoriteStore(store: StoreEntity) {
        store.isFavorite = !store.isFavorite
        doAsync {
            StoreApplication.database.storeDao().updateStore(store)
            uiThread {
                updateStore(store)
            }
        }
    }

    override fun onDeleteStore(store: StoreEntity) {
        val item = arrayOf("Eliminar", "Llamar", "Ir a la web")
        MaterialAlertDialogBuilder(this).setTitle(R.string.dialog_opcions_title).setItems(
            item
        ) { dialogInterface, i ->
            when (i) {
                0 -> confirmDeleteDialog(store)
                1 -> dial(store.phone)
                2 -> goToWebsite(store.website)
            }
        }.show()
    }

    private fun confirmDeleteDialog(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this).setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                doAsync {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    uiThread {
                        mAdapter.delete(storeEntity)
                    }
                }
            }.setNegativeButton(R.string.dialog_delete_cancel, null).show()
    }

    private fun dial(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        if(callIntent.resolveActivity(packageManager)!= null){

            startActivity(callIntent)
        }else{
            Toast.makeText(this, R.string.call_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToWebsite(website: String) {
        if (website.isEmpty()) {
            Toast.makeText(this, R.string.no_website, Toast.LENGTH_LONG).show()

        } else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            if(websiteIntent.resolveActivity(packageManager)!= null){

                startActivity(websiteIntent)
            }else{
                Toast.makeText(this, R.string.website_error, Toast.LENGTH_LONG).show()
            }
        }
    }

    /*
    * MainAux
    *
    * */
    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fabAdd.show() else mBinding.fabAdd.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}