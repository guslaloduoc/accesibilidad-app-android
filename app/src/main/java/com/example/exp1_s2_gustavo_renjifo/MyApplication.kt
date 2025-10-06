package com.example.exp1_s2_gustavo_renjifo

import android.app.Application
import com.example.exp1_s2_gustavo_renjifo.services.ServiceLocator

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.provide(this)
    }
}
