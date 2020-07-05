package com.testtube.gstreporter.model

import java.io.Serializable
import java.util.*

class SaleItem(
    val invoiceId: Long = System.currentTimeMillis(),
    var invoiceNumber: String = "IN-${invoiceId}",
    var gstNumber: String = "",
    var partyName: String = "",
    var taxableAmount: Double = 0.0,
    var date: Date = Date(),
    var sGST: Double = 0.0,
    var cGST: Double = 0.0,
    var iGST: Double = 0.0,
    var tGST: Double = sGST + cGST + iGST,
    var totalInvoiceAmount: Double = 0.0
) : Serializable {

    var invoiceCreatedDate: Date = Date()

}