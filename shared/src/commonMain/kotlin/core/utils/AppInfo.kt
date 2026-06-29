package core.utils

object AppInfo {
    // Valores inyectados en build time desde app/build.gradle.kts
    // Las plataformas pueden sobreescribir en runtime (Android usa BuildConfig, iOS usa NSBundle)
    var versionName: String = SharedBuildConfig.APP_VERSION_NAME
        internal set

    var versionCode: Int = SharedBuildConfig.APP_VERSION_CODE
        private set

    fun initialize(versionName: String, versionCode: Int) {
        this.versionName = versionName
        this.versionCode = versionCode
    }
}