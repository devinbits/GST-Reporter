package com.testtube.gstreporter.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.SaleItem

class SaleListVM(application: Application) : AndroidViewModel(application) {

    var sales: MutableLiveData<List<SaleItem>> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    private var itemCollectionAdapter: ItemCollectionAdapter = ItemCollectionAdapter(getApplication() as Context)

    fun getRecentDocuments(): MutableLiveData<List<SaleItem>> {
        loading.postValue(true)
        sales.postValue(ArrayList<SaleItem>())
        itemCollectionAdapter.getRecentDocuments()
            .addOnSuccessListener { querySnapshot ->
                sales.postValue(querySnapshot.documents.mapNotNull { document ->
                    document?.toObject(SaleItem::class.java)
                })
                loading.postValue(false)
            }
        return sales
    }

    fun deleteSale(id: String) {
        itemCollectionAdapter.deleteSaleItem(id)
    }
}