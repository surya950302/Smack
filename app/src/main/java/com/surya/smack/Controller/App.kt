package com.surya.smack.Controller

import android.app.Application
import com.surya.smack.Utilities.SharedPrefs

class App : Application() {

    companion object{
        lateinit var sp : SharedPrefs
    }
    override fun onCreate() {
        sp = SharedPrefs(applicationContext)
        super.onCreate()
    }
}