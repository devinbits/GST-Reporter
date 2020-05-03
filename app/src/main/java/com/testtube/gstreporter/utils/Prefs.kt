package com.testtube.gstreporter.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs {

    companion object {

        private const val userKey: String = "USER"

        private fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences("gst_prefs", Context.MODE_PRIVATE)
        }

        fun clearPrefs(context: Context) {
            getPrefs(context).edit().clear().apply()
        }

        fun getUser(context: Context): String? {
            return getPrefs(context).getString(userKey, null)
        }

        fun setUser(context: Context, userEmail: String) {
            getPrefs(context).edit().putString(userKey, userEmail).apply()
        }
    }
}