package com.testtube.gstreporter.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.AsyncTask
import android.widget.Toast
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

        @SuppressLint("SimpleDateFormat")
        public fun getFormattedDate(format: String, mDate: Date): String {
            val simpleDateFormat = SimpleDateFormat(format)
            return simpleDateFormat.format(mDate)
        }

        @JvmStatic
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

    }

//    init {
//        instance = this
//    }
}