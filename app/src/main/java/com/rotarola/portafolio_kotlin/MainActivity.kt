package com.rotarola.portafolio_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import core.utils.AppInfo
import di.androidModule
import di.commonModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import presentation.view.atoms.theme.FeatureUITheme
import presentation.view.pages.NavigationMain

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar versión de la app
        AppInfo.initialize(
            versionName = BuildConfig.VERSION_NAME,
            versionCode = BuildConfig.VERSION_CODE
        )

        // Inicializar Koin
        startKoin {
            androidContext(this@MainActivity)
            modules(commonModule, androidModule)
        }

        setContent {
            FeatureUITheme(){
                NavigationMain() // ✅ UI compartida desde shared
            }
        }
    }
}

