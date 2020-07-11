package com.testtube.gstreporter.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException


class Common {

    companion object {
//        @JvmStatic
//        lateinit var instance: Common

        public fun showToast(context: Context?, message: String?) {
            context?.let { Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
        }

        public fun showToast(context: Context?, message: Int) {
            context?.let { Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
        }

        @SuppressLint("SimpleDateFormat")
        public fun getFormattedDate(format: String, mDate: Date): String {
            val simpleDateFormat = SimpleDateFormat(format)
            return simpleDateFormat.format(mDate)
        }

        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        @Throws(ExecutionException::class, InterruptedException::class)
        public fun grabBitMapfromFileAsync(
            mContext: Context?,
            filePath: String?,
            thumbNailSize: Int
        ): Bitmap? {
            return object : AsyncTask<Void?, Void?, Bitmap?>() {
                override fun doInBackground(vararg params: Void?): Bitmap? {
                    val options = BitmapFactory.Options()
                    options.inSampleSize = Constants.imageSampleSize
                    var bitmap = BitmapFactory.decodeFile(filePath, options)
                    if (thumbNailSize > 0) bitmap = ThumbnailUtils.extractThumbnail(
                        bitmap,
                        thumbNailSize, thumbNailSize
                    )
                    return bitmap
                }

                override fun onPostExecute(aVoid: Bitmap?) {
                    super.onPostExecute(aVoid)
                }

            }.execute().get()
        }

        fun startPictureCaptureIntent(
            appCompatActivity: AppCompatActivity,
            mrequestId: Int,
            filename: String
        ): String? {
            var requestId = mrequestId
            if (requestId == 0) requestId = Constants.REQUEST_IMAGE_CAPTURE
            var photoFile: File? = null
            var fileAbsPath: String? = null
            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(appCompatActivity.packageManager) != null) {
                    // Create the File where the photo should go
                    // Continue only if the File was successfully created
                    photoFile = createImageFile(appCompatActivity)
                    if (photoFile != null) {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            appCompatActivity,
                            appCompatActivity.packageName + ".fileprovider",
                            photoFile
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        appCompatActivity.startActivityForResult(takePictureIntent, requestId)
                        fileAbsPath = photoFile.getAbsolutePath()
                    }
                }
            } catch (ee: IOException) {
                showToast(appCompatActivity, com.testtube.gstreporter.R.string.image_capture_error)
            }
            return fileAbsPath
        }

        fun startPictureCaptureIntentFragment(
            frag: Fragment,
            mrequestId: Int,
            filename: String
        ): String? {
            var requestId = mrequestId
            if (requestId == 0) requestId = Constants.REQUEST_IMAGE_CAPTURE_FRAG
            var photoFile: File? = null
            var fileAbsPath: String? = null
            val context = frag.context
            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (context != null) {
                    if (takePictureIntent.resolveActivity(context.packageManager) != null) {
                        // Create the File where the photo should go
                        // Continue only if the File was successfully created
                        photoFile = createImageFile(context)
                        if (photoFile != null) {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                context,
                                frag.requireContext().packageName + ".fileprovider",
                                photoFile
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            frag.startActivityForResult(takePictureIntent, requestId)
                            fileAbsPath = photoFile.getAbsolutePath()
                        }
                    }
                }
            } catch (ee: IOException) {
                showToast(context, com.testtube.gstreporter.R.string.image_capture_error)
            }
            return fileAbsPath
        }


        @Throws(IOException::class)
        fun createImageFile(
            mContext: Context,
            filename: String = "JPEG_" + System.currentTimeMillis()
        ): File? {
            // Create an image file name
            val storageDir =
                mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                filename,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
        }

    }

//    init {
//        instance = this
//    }
}