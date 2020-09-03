package com.testtube.gstreporter.firestoreController

import android.content.Context
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common
import com.testtube.gstreporter.utils.Constants
import java.util.*

class ItemCollectionAdapter(val context: Context) : OnFailureListener {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val root: String = "users/${Common.getUser()}"
    private val saleCollection: String = "${root}/sales"

    fun saveItem(saleItem: SaleItem): Task<Void> {
       return db.collection(saleCollection).document(saleItem.InvoiceId.toString()).set(saleItem)
            .addOnSuccessListener {
                Common.showToast(
                    context,
                    "Saved ${saleItem.Bill}"
                )
            }
    }

    fun getAllDocuments(): Task<QuerySnapshot> = db.collection(saleCollection).get()

    fun getRecentDocuments(count: Long = 100): Task<QuerySnapshot> =
        db.collection(saleCollection).limit(count).get()

    fun getDocumentsOrderedBy(count: Long = 100, orderBy: String): Task<QuerySnapshot> =
        db.collection(saleCollection).orderBy(orderBy, Query.Direction.DESCENDING).limit(count)
            .get()

    fun getDocuments(count: Long = 100, date: Date): Task<QuerySnapshot> =
        db.collection(saleCollection).whereEqualTo("sdate", Common.getSDate(date)).get()


    fun getDocuments(count: Long = 100, start: Date, end: Date): Task<QuerySnapshot> =
        db.collection(saleCollection).whereGreaterThanOrEqualTo("sdate", Common.getSDate(start))
            .whereLessThanOrEqualTo("sdate", Common.getSDate(end)).get()

    fun deleteSaleItem(id: String) {
        db.collection(saleCollection).document(id).delete().addOnFailureListener(this)
        val storage = Firebase.storage
        val storageRef = storage.reference
        val ref = storageRef.child("${Common.getUser()}/IN-${id}")
        ref.listAll()
            .addOnSuccessListener { listResult ->
                listResult.prefixes.forEach { prefix ->
                    prefix.listAll().addOnSuccessListener { it ->
                        it.items.forEach { item ->
                            item.delete()
                        }
                    }
                }

                listResult.items.forEach { item ->
                    item.delete()
                }
            }
    }

    override fun onFailure(p0: Exception) {
        Common.showToast(context, "Failed ${p0.message}")
    }

    fun getDocuments(
        count: Long,
        start: Date,
        filterType: Enum<Constants.FilterType>
    ): Task<QuerySnapshot> {
        return if (filterType == Constants.FilterType.Month) {
            val endDate = Date(start.time)
            endDate.month = start.month + 1
            getDocuments(count, start, endDate)
        } else getDocuments(count, start)

    }

}