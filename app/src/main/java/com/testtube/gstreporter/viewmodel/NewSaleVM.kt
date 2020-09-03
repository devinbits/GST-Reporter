package com.testtube.gstreporter.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.tasks.Task
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.Profile
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Constants
import com.testtube.gstreporter.workers.FirebaseStorageFileUpload

class NewSaleVM(application: Application) : AndroidViewModel(application) {

    private var workManager: WorkManager
    private var itemCollectionAdapter: ItemCollectionAdapter? = null
    var invoiceNumber = "100"
    var gstNumber = ""
    var partyName = ""
    var taxableAmount = ""
    var date = ""
    var sGST = ""
    var cGST = ""
    var iGST = ""
    var tGST = ""
    var totalInvoiceAmount = ""
    var totalGst = ""
    var isSameState: MutableLiveData<Boolean> = MutableLiveData(false)
    var profile: MutableLiveData<Profile> = MutableLiveData()

//    val includeImageCheckBox: MutableLiveData<Boolean> = MutableLiveData(false)

    var includeImageCheckBox: Boolean = false

    init {
        val context = getApplication() as Context
        itemCollectionAdapter = ItemCollectionAdapter(context)
        workManager = WorkManager.getInstance(context)
        Profile().getProfile(context).addOnSuccessListener {
            profile.postValue(it?.toObject(Profile::class.java) ?: Profile())
        }
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
            totalGst.toDouble(),
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

    fun checkIsSameState() {
        if (gstNumber.length < 2)
            return
        val sCode = gstNumber.substring(0, 2) ?: ""
        val states = Constants.States.values()
        if (sCode.isNotBlank()) {
            when (val code = sCode.toIntOrNull()) {
                null -> isSameState.value = false
                else -> {
                    isSameState.postValue(
                        if (code in 1..states.size)
                            states[code].name.replace("_", " ") == profile.value?.state
                        else false
                    )
                }
            }
        }
    }

}
