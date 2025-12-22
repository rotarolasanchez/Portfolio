package com.rotarola.portafolio_kotlin.core.utils

object AppInfo {
    // Valor por defecto hasta que la app lo setee en runtime
    var versionName: String = "0.0.0"
        internal set

    var versionCode: Int = 0
        private set

    fun initialize(versionName: String, versionCode: Int) {
        this.versionName = versionName
        this.versionCode = versionCode
    }
}