package com.testtube.gstreporter.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable
import java.util.*

class SaleItem(
    val InvoiceId: Long = System.currentTimeMillis(),
    var Invoice_Number: String = "IN-${InvoiceId}",
    var Gst_Number: String = "",
    var Party_Name: String = "",
    var Taxable_Amount: Double = 0.0,
    var Date: Date = getDate(),
    var sDate: Int = com.testtube.gstreporter.utils.Common.getSDate(getDate()),
    var sGST: Double = 0.0,
    var cGST: Double = 0.0,
    var iGST: Double = 0.0,
    var GST: Double = sGST + cGST + iGST,
    var Total_Invoice_Amount: Double = 0.0,
    @Exclude
    var images: List<String>? = null
) : Serializable {
}

private fun getDate(): Date {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    return cal.time
}
