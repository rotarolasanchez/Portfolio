package com.rotarola.portafolio_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import core.utils.AppInfo
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

        // Koin ya fue inicializado en PortafolioApplication.onCreate()
        setContent {
            FeatureUITheme {
                NavigationMain()
            }
        }
    }
}
