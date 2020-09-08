package com.testtube.gstreporter.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.*
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.testtube.gstreporter.BuildConfig
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
        public fun getFormattedDate(format: String, mDate: Date = Date()): String {
            val simpleDateFormat = SimpleDateFormat(format)
            return simpleDateFormat.format(mDate)
        }

        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        @Throws(ExecutionException::class, InterruptedException::class)
        public fun grabBitMapfromFileAsync(
            mContext: Context?,
            filePath: String?,
            thumbNailSize: Int = 0
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
            mRequestId: Int,
            filename: String
        ): String? {
            var requestId = mRequestId
            if (requestId == 0) requestId = Constants.REQUEST_IMAGE_CAPTURE
            var photoFile: File? = null
            var fileAbsPath: String? = null
            try {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(appCompatActivity.packageManager) != null) {
                    photoFile = createFile(
                        appCompatActivity, Constants.Ext.JPG,
                        Constants.DirType.Image,
                        "GST_${System.currentTimeMillis()}"
                    )
                    val photoURI: Uri = FileProvider.getUriForFile(
                        appCompatActivity,
                        "${appCompatActivity.packageName}.fileprovider",
                        photoFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    appCompatActivity.startActivityForResult(takePictureIntent, requestId)
                    fileAbsPath = photoFile.absolutePath
                }
            } catch (ee: IOException) {
                showToast(appCompatActivity, com.testtube.gstreporter.R.string.image_capture_error)
            }
            return fileAbsPath
        }

        fun startPictureCaptureIntentFragment(
            frag: Fragment,
            mRequestId: Int
        ): String? {
            var requestId = mRequestId
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
                        photoFile = createFile(
                            context,
                            Constants.Ext.JPG,
                            Constants.DirType.Image,
                            "GST_${System.currentTimeMillis()}"
                        )
                        val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "${frag.requireContext().packageName}.fileprovider",
                            photoFile
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        frag.startActivityForResult(takePictureIntent, requestId)
                        fileAbsPath = photoFile.absolutePath
                    }
                }
            } catch (ee: IOException) {
                showToast(context, com.testtube.gstreporter.R.string.image_capture_error)
            }
            return fileAbsPath
        }


        @Throws(IOException::class)
        fun createFile(
            mContext: Context,
            ext: String,
            dirType: String = Constants.DirType.Doc,
            filename: String
        ): File {
            // Create an image file name
            val storageDir =
                mContext.getExternalFilesDir(dirType)
            return File.createTempFile(
                filename,  /* prefix */
                "${ext}_",  /* suffix */
                storageDir /* directory */
            )
        }

        @JvmStatic
        fun getCircularCroppedImage(bitmap: Bitmap): Bitmap? {
            val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            canvas.drawCircle(
                (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
                (Math.min(bitmap.width, bitmap.height) / 2).toFloat(), paint
            )
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }

        @JvmStatic
        fun getUser(): String? {
            return FirebaseAuth.getInstance().currentUser?.email
        }

        fun getPixel(r: Resources, dip: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.displayMetrics
            )
        }

        fun sendEmail(context: Context, file: File) {
            try {
                val uri = FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
                )
//            val email = "adityanabhinav@outlook.com"
                val subject = "GST Report"
                val emailIntent = Intent(Intent.ACTION_SEND)
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(email))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                emailIntent.type = "message/*"
                context.startActivity(Intent.createChooser(emailIntent, "Sending email..."))
            } catch (t: Throwable) {
                showToast(context, "Request failed try again: $t")
            }
        }

        @JvmStatic
        fun getMonth(start: Date): Int {
            return getFormattedDate("M", start).toInt()
        }

        fun getSDate(start: Date): Int {
            return getFormattedDate("Mdd", start).toInt()
        }
    }
}