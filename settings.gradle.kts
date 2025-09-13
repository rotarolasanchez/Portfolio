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
include(":data")
include(":core")
include(":domain")
include(":feature")
include(":feature:login")
include(":feature:home")
include(":feature:chatbot")

