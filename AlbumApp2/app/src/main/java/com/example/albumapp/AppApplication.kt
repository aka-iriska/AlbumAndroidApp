package com.example.albumapp

import android.app.Application
import com.example.albumapp.data.AppContainer
import com.example.albumapp.data.AppDataContainer

class AppApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}