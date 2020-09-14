package com.testtube.gstreporter.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.tasks.Task
import com.testtube.gstreporter.database.MyDatabase
import com.testtube.gstreporter.database.PartyInfo
import com.testtube.gstreporter.firestoreController.ItemCollectionAdapter
import com.testtube.gstreporter.model.Profile
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Constants
import com.testtube.gstreporter.workers.FirebaseStorageFileUpload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewSaleVM(application: Application) : AndroidViewModel(application) {

    private var workManager: WorkManager
    private var itemCollectionAdapter: ItemCollectionAdapter? = null
    var invoiceNumber = ""
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
    private lateinit var partyInfoList: LiveData<List<PartyInfo>>

//    val includeImageCheckBox: MutableLiveData<Boolean> = MutableLiveData(false)

    var includeImageCheckBox: Boolean = false

    init {
        val context = getApplication() as Context
        itemCollectionAdapter = ItemCollectionAdapter(context)
        workManager = WorkManager.getInstance(context)
        Profile().getProfile(context).addOnSuccessListener {
            profile.postValue(it?.toObject(Profile::class.java) ?: Profile())
        }
        getGstPartyList()
    }

    fun getPartyInfo() = partyInfoList

    fun saveItem(saleItem: SaleItem): Task<Void>? {
        val mSaleItem = getSaleItem(saleItem)
        saveGstPartyInfo(mSaleItem.Party_GSTN, mSaleItem.Party_Name)
        mSaleItem.images?.forEach { path ->
            val data = Data.Builder()
                .putString("path", path)
                .putString("inv", invoiceNumber)
                .build()
            val req = OneTimeWorkRequestBuilder<FirebaseStorageFileUpload>()
                .setInputData(data)
                .build()
            workManager.enqueue(req)
        }
        return itemCollectionAdapter?.saveItem(mSaleItem)
    }

    fun getSaleItem(saleItem: SaleItem): SaleItem =
        SaleItem(
            invoiceNumber,
            saleItem.InvoiceId,
            gstNumber,
            partyName,
            taxableAmount.toDouble(),
            saleItem.Date,
            saleItem.sDate,
            sGST.toDoubleOrNull() ?: 0.0,
            cGST.toDoubleOrNull() ?: 0.0,
            iGST.toDoubleOrNull() ?: 0.0,
            tGST.toDoubleOrNull() ?: 0.0,
            totalGst.toDoubleOrNull() ?: 0.0,
            totalInvoiceAmount.toDoubleOrNull() ?: 0.0
        )

    fun checkIsSameState() {
        if (gstNumber.length < 2)
            return
        val sCode = gstNumber.substring(0, 2)
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


    private fun saveGstPartyInfo(gstIN: String, partyName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val partyDao = MyDatabase.getDatabase(getApplication()).partyDao()
            partyDao.insert(PartyInfo(gstIN, partyName))
        }
    }

    private fun getGstPartyList() {
        val partyDao = MyDatabase.getDatabase(getApplication()).partyDao()
        partyInfoList = partyDao.getAll()
    }

    fun getPartyName(gstId: String? = gstNumber): PartyInfo? {
        return if (gstNumber.length == 15)
            partyInfoList.value?.find { pi -> pi.gstId == gstId }
        else null
    }

}
