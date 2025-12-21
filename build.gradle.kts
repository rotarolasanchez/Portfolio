plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    //alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.24" apply false
}



