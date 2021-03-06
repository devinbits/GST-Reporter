package com.testtube.gstreporter.model

import android.os.Parcelable
import androidx.databinding.BaseObservable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
class SaleItem(
    var Bill: String = "1001",
    var InvoiceId: String = "${System.currentTimeMillis()}",
    var Party_GSTN: String = "",
    var Party_Name: String = "",
    var Bill_Amount: Double = 0.0,
    var Date: Date = getDate(),
    var sDate: Int = com.testtube.gstreporter.utils.Common.getSDate(getDate()),
    var sGST: Double = 0.0,
    var cGST: Double = 0.0,
    var iGST: Double = 0.0,
    var GST_Percentage: Double = 0.0,
    var Total_GST: Double = sGST + cGST + iGST,
    var Total_Invoice_Value: Double = 0.0,
    var images: List<String>? = null
) : Serializable, BaseObservable(), Parcelable

private fun getDate(): Date {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    return cal.time
}
