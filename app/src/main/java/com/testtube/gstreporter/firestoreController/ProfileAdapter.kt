package com.testtube.gstreporter.firestoreController

import android.content.Context
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.testtube.gstreporter.model.Profile
import com.testtube.gstreporter.utils.Common

class ProfileAdapter(val context: Context) : OnFailureListener {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val root: String = "users/${Common.getUser()}"
    private val saleCollection: String = "${root}/sales"

    fun saveProfile(profile: Profile) {
        db.document(root).set(profile)
            .addOnSuccessListener { void ->
                Common.showToast(
                    context,
                    "Saved ${profile.name}"
                )
            }
    }

    fun getProfile(): Task<DocumentSnapshot> = db.document(root).get()

    override fun onFailure(p0: Exception) {
        Common.showToast(context, "Failed ${p0.message}")
    }

}