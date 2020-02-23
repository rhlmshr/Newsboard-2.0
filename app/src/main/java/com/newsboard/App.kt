package com.newsboard

import android.app.Application
import android.content.Context
import com.newsboard.data.local.AppDatabase

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        context = applicationContext

        AppDatabase.invoke(context)
    }

    companion object {
        lateinit var context: Context
    }
}