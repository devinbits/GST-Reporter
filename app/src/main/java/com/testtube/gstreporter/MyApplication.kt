package com.testtube.gstreporter

import android.app.Application
import com.testtube.gstreporter.database.MyDatabase

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MyDatabase.getDatabase(this)
    }

}