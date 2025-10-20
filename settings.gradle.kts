pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("<https://jitpack.io>")
    }
}


rootProject.name = "portafolio_kotlin"
include(":app")
//include(":core")
//include(":core:data")
//include(":test")
//include(":feature")
//include(":feature:feature-menu")
//include(":feature:feature-login")
//include(":feature:feature-UI")
