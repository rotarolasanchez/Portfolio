package com.rotarola.portafolio_kotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rotarola.portafolio_kotlin.presentation.view.pages.NavigationMain
import com.rotarola.portafolio_kotlin.presentation.view.theme.FeatureUITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContent {
                FeatureUITheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavigationMain()
                    }
                }
            }
        } catch (e: Exception) {
            setContent {
                FeatureUITheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error al cargar la aplicación")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onDestroy", e)
        }
    }
}