# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Mantener información de número de línea para depuración de stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Application y Activity principales
-keep class com.rotarola.portafolio_kotlin.PortafolioApplication { *; }
-keep class com.rotarola.portafolio_kotlin.MainActivity { *; }

# Koin - inyección de dependencias
-keep class org.koin.** { *; }
-keepnames class org.koin.** { *; }
-keep class di.** { *; }

# ViewModels compartidos (shared module)
-keep class presentation.viewmodels.** { *; }

# Domain / Data / Core (shared module)
-keep class domain.** { *; }
-keep class data.** { *; }
-keep class core.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Kotlinx Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Modelos de datos
-keep class domain.model.** { *; }
