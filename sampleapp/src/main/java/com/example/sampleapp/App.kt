package com.example.sampleapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        var appContext: Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        
        appContext = this
        AndroidThreeTen.init(this)
        FirebaseApp.initializeApp(this)

        //YoumatterSDK.getInstance(appContext!!).checkDeepLink(appContext!!)
    }


}