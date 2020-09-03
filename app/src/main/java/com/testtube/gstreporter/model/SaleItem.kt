package com.testtube.gstreporter.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.util.*

class SaleItem(
    @Bindable val InvoiceId: Long = System.currentTimeMillis(),
    @Bindable var Bill: String = "IN-${InvoiceId}",
    @Bindable var Party_GSTN: String = "",
    @Bindable var Party_Name: String = "",
    @Bindable var Bill_Amount: Double = 0.0,
    @Bindable var Date: Date = getDate(),
    @Bindable var sDate: Int = com.testtube.gstreporter.utils.Common.getSDate(getDate()),
    @Bindable var sGST: Double = 0.0,
    @Bindable var cGST: Double = 0.0,
    @Bindable var iGST: Double = 0.0,
    @Bindable var GST_Percentage: Double = 0.0,
    @Bindable var Total_GST: Double = sGST + cGST + iGST,
    @Bindable var Total_Invoice_Value: Double = 0.0,
    @Exclude
    var images: List<String>? = null
) : Serializable, BaseObservable() {
}

private fun getDate(): Date {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    return cal.time
}
