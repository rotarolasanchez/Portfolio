package com.rotarola.portafolio_kotlin

import android.app.Application
import com.rotarola.portafolio_kotlin.core.utils.AppInfo

// app/src/main/java/com/rotarola/portafolio_kotlin/PortafolioApplication.kt
class PortafolioApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInfo.initialize(
            versionName = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE
        )
    }
}