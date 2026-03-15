plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.compose) apply false
    //alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.jetbrains.kotlin.multiplatform) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.ksp) apply false
}

// ✅ Forzar versiones seguras de webpack y webpack-dev-server
// CVE-2025-68157, CVE-2025-68458 → webpack >= 5.104.1
// CVE-2025-30359, CVE-2025-30360 → webpack-dev-server >= 5.2.1
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().apply {
        resolution("webpack", "5.104.1")
        resolution("webpack-dev-server", "5.2.1")
    }
}