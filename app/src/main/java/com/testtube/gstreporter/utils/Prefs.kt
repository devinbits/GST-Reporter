package com.testtube.gstreporter.utils

import android.content.Context
import android.content.SharedPreferences

class Prefs {

    companion object {

        private const val userEmail: String = "USER_EMAIL"
        private const val userId: String = "USER_ID"

        private fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences("gst_prefs", Context.MODE_PRIVATE)
        }

        fun clearPrefs(context: Context) {
            getPrefs(context).edit().clear().apply()
        }

        fun getUser(context: Context): String? {
            return getPrefs(context).getString(userEmail, null)
        }

        fun setUser(context: Context, userEmail: String) {
            getPrefs(context).edit().putString(userEmail, userEmail).apply()
        }

        fun getUserId(context: Context): String? {
            return getPrefs(context).getString(userId, null)
        }

        fun setUserId(context: Context, userEmail: String) {
            getPrefs(context).edit().putString(userId, userEmail).apply()
        }
    }
}