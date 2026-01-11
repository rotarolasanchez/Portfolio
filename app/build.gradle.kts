
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    //alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    id("io.realm.kotlin") version libs.versions.realm.get()
    id("org.sonarqube") version libs.versions.sonarqube.get()
    id ("jacoco")
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("testDebugUnitTest"))

    reports {
        xml.required.set(true)
        xml.outputLocation.set(file("${buildDir}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"))
        html.required.set(true)
    }

    classDirectories.setFrom(
        fileTree("${buildDir}/tmp/kotlin-classes/debug") {
            include("**/*.class")
            exclude(
                "**/di/**",
                "**/hilt_aggregated_deps/**",
                "**/*_Factory*"
            )
        }
    )
    sourceDirectories.setFrom(
        files("src/main/java", "src/main/kotlin")
    )
    executionData.setFrom(files("${buildDir}/jacoco/testDebugUnitTest.exec"))
}

tasks.withType<Test> {
    finalizedBy(tasks.named("jacocoTestReport"))
}

// Configuración para leer las API keys desde local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { inputStream ->
        localProperties.load(inputStream)
    }
}

android {
    namespace = "com.rotarola.portafolio_kotlin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rotarola.portafolio_kotlin"
        minSdk = 24
        targetSdk = 35
        versionCode = 12
        versionName = "2.7.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Configurar BuildConfig fields para las API keys
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY", "")}\"")

        // Opcional: Si quieres tener diferentes keys para debug/release
        // buildConfigField("String", "GEMINI_API_KEY_DEBUG", "\"${localProperties.getProperty("GEMINI_API_KEY_DEBUG", "")}\"")
        // Configurar BuildConfig fields para las API keys
        buildConfigField("String", "MODEL_NAME", "\"${localProperties.getProperty("MODEL_NAME", "")}\"")
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    sonar {
        properties {
            property("sonar.projectKey", "Portfolio_kotlin")
            property("sonar.organization", "")
            property("sonar.host.url", "https://sonarqube.capibarafamily.online/")
            property("sonar.token", "SONAR_TOKEN")
            property("sonar.coverage.jacoco.xmlReportPaths", layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.xml").get().asFile.absolutePath)
            property("sonar.junit.reportPaths", layout.buildDirectory.dir("test-results/test").get().asFile.absolutePath)
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false // ✅ Habilitar ofuscación
            isShrinkResources = false // ✅ Eliminar recursos no usados
            // Configuración específica para debug si es necesario
            buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY_DEBUG", localProperties.getProperty("GEMINI_API_KEY", ""))}\"")
            isDebuggable = true
        }

        release {
            isMinifyEnabled = true // ✅ Habilitar ofuscación
            isShrinkResources = true // ✅ Eliminar recursos no usados
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            // Usar la misma key para release o una diferente
            buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY_RELEASE", localProperties.getProperty("GEMINI_API_KEY", ""))}\"")
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

configurations.all {
    resolutionStrategy {
        force("com.google.protobuf:protobuf-java:3.21.12") // O la versión que funcione en tu caso
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":feature:login"))
    implementation(project(":feature:menu"))
    implementation(project(":feature:chatbot"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.ui.test.junit4.android)
    implementation(libs.androidx.runner)
    implementation(libs.generativeai)
    implementation(libs.androidx.room.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.junit.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)

    //hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    //kapt(libs.hilt.android.compiler)
    ksp(libs.hilt.android.compiler)
    androidTestImplementation(libs.hilt.android.testing)
    //kaptAndroidTest(libs.hilt.android.compiler)
    kspAndroidTest(libs.hilt.android.compiler)
    //androidTestAnnotationProcessor(libs.hilt.android.compiler)

    //Realm
    implementation(libs.realm.base)
    implementation(libs.realm.sync)

    implementation(libs.kotlinx.stdlib.jdk8)
    implementation(libs.lottie.compose)

    //junit
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.coroutines.test)

    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")


    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Google sign-in vía Credential Manager
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    //kapt("com.google.dagger:hilt-compiler:2.51.1")


    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Hilt
    implementation(libs.hilt.android)
    //kapt(libs.hilt.android.compiler)
}