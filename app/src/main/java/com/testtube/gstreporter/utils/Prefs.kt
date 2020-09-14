package com.testtube.gstreporter.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs {

    companion object {

        private fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences("gst_prefs", Context.MODE_PRIVATE)
        }

        fun clearPrefs(context: Context) {
            getPrefs(context).edit().clear().apply()
        }

        fun setProfileName(context: Context, name: String) {
            getPrefs(context).edit().putString("USERNAME", name).apply()
        }

        fun getProfileName(context: Context): String {
            return getPrefs(context).getString("USERNAME", "")!!
        }

    }
}