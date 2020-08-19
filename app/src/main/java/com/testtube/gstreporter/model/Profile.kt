package com.testtube.gstreporter.model

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.testtube.gstreporter.firestoreController.ProfileAdapter

class Profile(
    var name: String = "",
    var firmName: String = "",
    var gstNumber: String = "",
    var state: String = "",
    var avatar: String = ""
) {

    fun getProfile(context: Context): Task<DocumentSnapshot> {
        return ProfileAdapter(context).getProfile()
    }

    fun saveProfile(context: Context) {
        ProfileAdapter(context).saveProfile(this)
    }

}