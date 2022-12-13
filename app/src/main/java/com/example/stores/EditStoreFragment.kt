package com.example.stores

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {
    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong(getString(R.string.arg_id), 0)
        if (id != null && id != 0L) {
            mIsEditMode = true
            getStore(id)
        } else {
            mIsEditMode = false
            mStoreEntity = StoreEntity(name = "", phone = "", photoUrl = "")

        }
        mActivity = activity as MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)
        setHasOptionsMenu(true)

        mBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this).load(mBinding.etPhotoUrl.text.toString()).diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).centerCrop().into(mBinding.imgPhoto)
        }

    }

    private fun getStore(id: Long) {
        doAsync {
            mStoreEntity = StoreApplication.database.storeDao().getStoreById(id)
            uiThread {
                if (mStoreEntity != null) setUiStore(mStoreEntity!!)
            }
        }
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding) {
            etName.text = (storeEntity.name).editable()
            etPhone.text = storeEntity.phone.editable()
            etWebsite.text = (storeEntity.website).editable()
            etPhotoUrl.text = (storeEntity.photoUrl).editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                if (mStoreEntity != null && validateFields()) {

                    with(mStoreEntity!!) {
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    doAsync {
                        if (mIsEditMode)
                            StoreApplication.database.storeDao()
                                .updateStore(mStoreEntity!!)
                        else
                            mStoreEntity!!.id =
                                StoreApplication.database.storeDao().addStore(mStoreEntity!!)
                        uiThread {
                            hideKeyboard()
                            if (mIsEditMode) {
                                mActivity!!.updateStore(mStoreEntity!!)
                                Snackbar.make(
                                    mBinding.root,
                                    R.string.updated_store,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                mActivity?.addStore(mStoreEntity!!)
                                Snackbar.make(
                                    mBinding.root,
                                    R.string.created_store,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
//
                            mActivity?.onBackPressed()
                        }
                    }
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

        //return super.onOptionsItemSelected(item)

    }

    private fun validateFields(): Boolean {
        var isValid = true
        /*if(mBinding.etPhotoUrl.text.toString().trim().isEmpty()){
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        }*/
        if(mBinding.etWebsite.text.toString().trim().isEmpty()){
            mBinding.tilWebsite.error = getString(R.string.helper_required)
            mBinding.etWebsite.requestFocus()
            isValid = false
        }
        if(mBinding.etPhone.text.toString().trim().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.etPhone.requestFocus()
            isValid = false
        }
        if(mBinding.etName.text.toString().trim().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid = false
        }
        return isValid

    }

    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)

    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)
        setHasOptionsMenu(false)
        super.onDestroy()
    }
}