package com.rotarola.portafolio_kotlin

import android.app.Application
import di.androidModule
import di.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PortafolioApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar Koin una sola vez en Application (NO en MainActivity)
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@PortafolioApplication)
            modules(commonModule, androidModule)
        }
    }
}
