package com.testtube.gstreporter.firestoreController

import android.content.Context
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.testtube.gstreporter.model.SaleItem
import com.testtube.gstreporter.utils.Common

class ItemCollectionAdapter(val context: Context) : OnFailureListener {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val root: String = "users/${Common.getUser()}"
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
            .addOnFailureListener {
                // Uh-oh, an error occurred!
            }
    }

    override fun onFailure(p0: Exception) {
        Common.showToast(context, "Failed ${p0.message}")
    }

}