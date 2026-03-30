plugins {
    alias(libs.plugins.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform) // 👈 AGREGAR
    alias(libs.plugins.compose.compiler)      // 👈 AGREGAR
    // Remover KSP y Hilt para usar solo Koin
}



kotlin {


    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
                }
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    // ✅ Habilitar wasmJs para soporte Web
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Agregar la carpeta de recursos
                        add(project.projectDir.path + "/src/wasmJsMain/resources/")
                    }
                }
            }
        }
        binaries.executable()
    }

    jvm("desktop") // 👈 Target para Desktop



    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
        }

        commonMain.dependencies {
            // ✅ Compose Multiplatform (100% compatible)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // navigation-compose removido de commonMain — tenía bug en iOS con CMP 1.7.1
            // Cada plataforma define su propia navegación vía expect/actual NavigationMain()

            // ✅ Koin para DI en KMP - versión estable
            implementation(libs.koin.core)
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
        }

        androidMain.dependencies {
            // ✅ Solo para Android si necesitas APIs específicas
            implementation(libs.androidx.core.ktx)
            // navigation-compose solo para Android — alpha tiene bug en iOS
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
            implementation(libs.androidx.appcompat)
            implementation(libs.material)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            // Firebase (solo Android)
            implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
            implementation("com.google.firebase:firebase-auth-ktx")

            // OkHttp (solo Android)
            implementation("com.squareup.okhttp3:okhttp:4.12.0")

            // Corrutinas Android
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

            // ML Kit para reconocimiento de texto
            implementation("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

            // Firebase Firestore
            implementation("com.google.firebase:firebase-firestore-ktx")

            // CameraX
            implementation("androidx.camera:camera-camera2:1.3.4")
            implementation("androidx.camera:camera-lifecycle:1.3.4")
            implementation("androidx.camera:camera-view:1.3.4")

            // 👇 Agregar para resolver ListenableFuture
            implementation("androidx.concurrent:concurrent-futures:1.2.0")
            implementation("com.google.guava:guava:32.1.3-android")
        }

        iosMain.dependencies {
            implementation(libs.koin.core)
            // koin-compose provee koinInject() para Compose Multiplatform en iOS
            implementation("io.insert-koin:koin-compose:4.0.0")
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.koin.core)
            }
        }

        // ✅ Dependencias para wasmJs (Web)
        val wasmJsMain by getting {
            dependencies {
                implementation(libs.koin.core)
                // Dependencias específicas para Web si son necesarias
            }
        }
    }
}

android {
    namespace = "com.example.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}


compose.desktop {
    application {
        mainClass = "MainKt"
    }
}


