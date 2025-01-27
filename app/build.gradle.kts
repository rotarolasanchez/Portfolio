
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    //alias(libs.plugins.sonarqube.plugin)
    id("io.realm.kotlin") version libs.versions.realm.get() // Realm plugin
    id("org.sonarqube") version libs.versions.sonarqube.get()
    id ("jacoco")
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.named("testDebugUnitTest"))

    reports {
        xml.required.set(true)
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
/*
tasks.register("verifyJacocoReport") {
    dependsOn("jacocoTestReport")
    doLast {
        val reportFile = file("${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
        if (!reportFile.exists()) {
            throw GradleException("Jacoco report not found: ${reportFile.absolutePath}")
        } else {
            println("Jacoco report generated successfully: ${reportFile.absolutePath}")
        }
    }
}

tasks.named("check") {
    dependsOn("verifyJacocoReport")
}*/

android {
    namespace = "com.rotarola.portafolio_kotlin"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.rotarola.portafolio_kotlin"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "2.0.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    sonar {
        properties {
            property("sonar.projectKey", "rotarolasanchez_Portfolio")
            property("sonar.organization", "rotarolasanchez")
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.token", "SONAR_TOKEN")
            property("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
            property("sonar.junit.reportPaths", "${buildDir}/test-results/test")
        }
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    /*packagingOptions {
        exclude("META-INF/gradle/incremental.annotation.processors")
    }*/

    packaging {
        resources.excludes.add("META-INF/gradle/incremental.annotation.processors")
    }


}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    //debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //debugImplementation(libs.ui.tooling)

    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)

    implementation(libs.realm.base)
    implementation(libs.realm.sync)

    implementation(libs.kotlinx.stdlib.jdk8)
    implementation(libs.lottie.compose)

    //test
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.coroutines.test)


}
