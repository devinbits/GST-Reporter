package com.testtube.gstreporter.workers

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.testtube.gstreporter.utils.Common
import java.io.ByteArrayOutputStream
import java.io.File


class FirebaseStorageFileUpload(
    appContext: Context,
    workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {

        if (inputData.getBoolean("is-avatar", false)) {
            uploadAvatar(inputData)
        } else
            uploadInvoiceImages(inputData)


        return Result.success()
    }

    private fun uploadInvoiceImages(input: Data) {
        val path: String = input.getString("path")!!
        val invoice: String = input.getString("inv")!!
        val storage = Firebase.storage
        val storageRef = storage.reference
        val file = Uri.fromFile(File(path))
        val ref = storageRef.child("${Common.getUser()}/${invoice}/${file.lastPathSegment}")
        val refThumb =
            storageRef.child("${Common.getUser()}/${invoice}/thumb/${file.lastPathSegment}")

        val uploadTask = ref.putFile(file)

        val thumbNail = Common.getCircularCroppedImage(
            Common.grabBitMapfromFileAsync(
                applicationContext,
                path,
                96
            )!!
        )
        val baos = ByteArrayOutputStream()
        thumbNail?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        refThumb.putBytes(data)

        uploadTask.continueWithTask {
            if (!it.isSuccessful) {
                Log.e(javaClass.simpleName, "upload-image-exp: ", it.exception)
            }
            it.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e(javaClass.simpleName, "download-url: success")
                } else {
                    Log.e(javaClass.simpleName, "download-url-exp: ", task.exception)
                }
            }
        }
    }

    private fun uploadAvatar(input: Data) {
        val path: String = input.getString("path")!!
        val storage = Firebase.storage
        val storageRef = storage.reference
        val file = Uri.fromFile(File(path))
        val refThumb = storageRef.child("${Common.getUser()}/avatar.${file.lastPathSegment?.split(".")
            ?.get(1)}")

        val thumbNail = Common.getCircularCroppedImage(
            Common.grabBitMapfromFileAsync(
                applicationContext,
                path,0
            )!!
        )
        val baos = ByteArrayOutputStream()
        thumbNail?.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        val uploadThumbTask = refThumb.putBytes(data)

        uploadThumbTask.continueWithTask {
            if (!it.isSuccessful) {
                Log.e(javaClass.simpleName, "upload-image-exp: ", it.exception)
            }
            it.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.e(javaClass.simpleName, "download-url: success")
                } else {
                    Log.e(javaClass.simpleName, "download-url-exp: ", task.exception)
                }
            }
        }
    }

}