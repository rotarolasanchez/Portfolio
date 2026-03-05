import org.gradle.kotlin.dsl.maven

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()

        // Repositorio para Binaryen (requerido para wasmJs)
        ivy {
            name = "Binaryen Distributions"
            setUrl("https://github.com/WebAssembly/binaryen/releases/download")
            patternLayout {
                artifact("version_[revision]/binaryen-version_[revision]-[classifier].[ext]")
            }
            metadataSources { artifact() }
            content { includeModule("com.github.webassembly", "binaryen") }
        }

        // Alternativa: usar Maven para binaryen
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")

        // Repositorio para Node.js
        ivy {
            name = "Node.js Distributions"
            setUrl("https://nodejs.org/dist")
            patternLayout {
                artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
            }
            metadataSources { artifact() }
            content { includeModule("org.nodejs", "node") }
        }

        // Repositorio para Yarn
        ivy {
            name = "Yarn Distributions"
            setUrl("https://github.com/yarnpkg/yarn/releases/download")
            patternLayout {
                artifact("v[revision]/[artifact](-v[revision]).[ext]")
            }
            metadataSources { artifact() }
            content { includeModule("com.yarnpkg", "yarn") }
        }
    }
}


rootProject.name = "portafolio_kotlin"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":shared")
