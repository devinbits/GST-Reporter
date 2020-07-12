package com.testtube.gstreporter.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs {

    companion object {

        private fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences("gst_prefs", Context.MODE_PRIVATE)
        }

        @JvmStatic
        fun clearPrefs(context: Context) {
            getPrefs(context).edit().clear().apply()
        }

    }
}