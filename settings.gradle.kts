pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("<https://jitpack.io>")
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
}


rootProject.name = "portafolio_kotlin"
include(":app")
include(":core")
include(":core:data")
include(":test")
include(":feature")
include(":feature:feature-menu")
include(":feature:feature-login")
include(":feature:feature-UI")
