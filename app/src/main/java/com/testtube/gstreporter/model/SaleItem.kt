package com.testtube.gstreporter.model

import java.io.Serializable
import java.util.*

class SaleItem(
    var invoiceNumber: String = "IN-${System.currentTimeMillis()}",
    var gstNumber: String = "",
    var partyName: String = "",
    var taxableAmount: Double = Double.MIN_VALUE,
    var date: Date = Date(),
    var sGST: Double = Double.MIN_VALUE,
    var cGST: Double = Double.MIN_VALUE,
    var iGST: Double = Double.MIN_VALUE,
    var tGST: Double = sGST + cGST + iGST,
    var totalInvoiceAmount: Double = Double.MIN_VALUE
) : Serializable {

    var invoiceId: Long = System.currentTimeMillis()

    var invoiceCreatedDate: Date = Date()

}