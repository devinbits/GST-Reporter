package com.testtube.gstreporter.firestoreController

import android.content.Context
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.Prefs

class ItemCollectionAdapter(val context: Context) : OnFailureListener {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val root: String = "users/${Prefs.getUserId(context)}"
    private val saleCollection: String = "${root}/sales"

    fun saveItem(saleItem: SaleItem) {
        db.collection(saleCollection).document(saleItem.invoiceId.toString()).set(saleItem)
            .addOnSuccessListener { void ->
                Common.showToast(
                    context,
                    "Saved ${saleItem.invoiceNumber}"
                )
            }
    }

    fun getAllDocuments(): Task<QuerySnapshot> = db.collection(saleCollection).get()

    fun deleteSaleItem(id: String) {
        db.collection(saleCollection).document(id).delete();
    }

    override fun onFailure(p0: Exception) {
        Common.showToast(context, "Failed ${p0.message}")
    }

}