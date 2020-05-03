package com.testtube.gstreporter.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

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

    }

//    init {
//        instance = this
//    }
}