package com.testtube.gstreporter.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.Filter
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.views.frag.SaleListFrag

class SaleListVM(application: Application) : AndroidViewModel(application) {

    var sales: MutableLiveData<List<SaleItem>> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData(false)
    private var itemCollectionAdapter: ItemCollectionAdapter =
        ItemCollectionAdapter(getApplication() as Context)
    var filter: MutableLiveData<Filter?> = MutableLiveData(null)

    init {
        loadDocuments()
    }

    private fun loadDocuments(): MutableLiveData<List<SaleItem>> {
        loading.postValue(true)
        sales.postValue(ArrayList<SaleItem>())
        itemCollectionAdapter.getRecentDocuments()
            .addOnSuccessListener { querySnapshot ->
                sales.postValue(querySnapshot.documents.mapNotNull { document ->
                    document?.toObject(SaleItem::class.java)
                }.asReversed())
                loading.postValue(false)
            }
        return sales
    }


    fun getRecentDocuments(): MutableLiveData<List<SaleItem>> = sales

    fun getFilteredDocDocuments(mFilter: Filter?) {
        sales.postValue(ArrayList())
        filter.postValue(mFilter)
        if (mFilter == null) {
            loadDocuments()
            return
        }
        loading.postValue(true)
        val startDate = mFilter.startDate
        val endDate = mFilter.endDate
        val filterType = mFilter.filterType
        if (mFilter.isRange) {
            if (startDate != null && endDate != null) {
                itemCollectionAdapter.getDocuments(100, startDate, endDate).addOnCompleteListener {
                    when (it.isSuccessful) {
                        true -> {
                            sales.postValue(it.result?.documents?.mapNotNull { snapshot ->
                                snapshot.toObject(SaleItem::class.java)
                            })
                        }
                    }
                    loading.postValue(false)
                }
            }
        } else if (startDate != null) {
            itemCollectionAdapter.getDocuments(100, startDate, filterType).addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        sales.postValue(it.result?.documents?.mapNotNull { snapshot ->
                            snapshot.toObject(SaleItem::class.java)
                        })
                    }
                    else -> {
                        Log.e(SaleListFrag::class.simpleName, "onApply: ", it.exception)
                    }
                }
                loading.postValue(false)
            }
        }
    }

    fun deleteSale(pos: Int, id: String) {
        val list = ArrayList<SaleItem>(sales.value!!)
        list.removeAt(pos)
        sales.postValue(list)
        itemCollectionAdapter.deleteSaleItem(id)
    }

    fun getLastDocument(): SaleItem? {
        val size = (sales.value?.size ?: 0)
        return if (size > 0) {
            val saleItem = sales.value!![0]
            val billNumber = saleItem.Bill.toIntOrNull()?.inc() ?: 1000
            SaleItem(
                Bill = "$billNumber",
                Party_GSTN = saleItem.Party_GSTN,
                Party_Name = saleItem.Party_Name
            )
        } else null
    }
}