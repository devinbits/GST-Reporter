package com.testtube.gstreporter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.tasks.Task
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.workers.FirebaseStorageFileUpload

class NewSaleVM : ViewModel() {

    private lateinit var workManager: WorkManager
    private var itemCollectionAdapter: ItemCollectionAdapter? = null
    var invoiceNumber = "100"
    var gstNumber = ""
    var partyName = ""
    var taxableAmount = ""
    var date = ""
    var sGST = "0.0"
    var cGST = "0.0"
    var iGST = "0.0"
    var tGST = "0.0"
    var totalInvoiceAmount = "0.0"

    fun init(itemCollectionAdapter: ItemCollectionAdapter, workManager: WorkManager) {
        this.itemCollectionAdapter = itemCollectionAdapter
        this.workManager = workManager
    }

    fun saveItem(saleItem: SaleItem): Task<Void>? {
        val mSaleItem = SaleItem(
            saleItem.InvoiceId,
            invoiceNumber,
            gstNumber,
            partyName,
            taxableAmount.toDouble(),
            saleItem.Date,
            saleItem.sDate,
            sGST.toDoubleOrNull() ?: 0.0,
            cGST.toDoubleOrNull() ?: 0.0,
            iGST.toDoubleOrNull() ?: 0.0,
            tGST.toDouble(),
            totalInvoiceAmount.toDouble()
        )
        mSaleItem.images?.forEach { path ->
            val data = Data.Builder()
                .putString("path", path)
                .putString("inv", invoiceNumber)
                .build();
            val req = OneTimeWorkRequestBuilder<FirebaseStorageFileUpload>()
                .setInputData(data)
                .build()
            workManager.enqueue(req)
        }
       return itemCollectionAdapter?.saveItem(mSaleItem)
    }

}
