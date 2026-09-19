package com.example.apptreino

import android.app.Application
import com.example.apptreino.di.AppContainer

class TreinosApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
