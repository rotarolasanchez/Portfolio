package com.rotarola.portafolio_kotlin.core.utils

object AppInfo {
    var versionName: String = ""
        private set

    var versionCode: Int = 0
        private set

    fun initialize(versionName: String, versionCode: Int) {
        this.versionName = versionName
        this.versionCode = versionCode
    }
}