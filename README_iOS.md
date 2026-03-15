# Guía de Configuración iOS — Portafolio Kotlin KMP

## Requisitos previos

| Requisito | Versión mínima |
|---|---|
| macOS | Ventura 13.0+ |
| Xcode | 15.0+ |
| JDK | 17+ |
| Android Studio | Hedgehog+ (opcional, para editar Kotlin) |

> ⚠️ **iOS solo se puede compilar desde macOS.** Todos los archivos Kotlin están
> preparados. Solo necesitas seguir esta guía desde una Mac.

---

## Estructura de archivos creados

```
portafolio_kotlin/
├── shared/
│   └── src/
│       └── iosMain/
│           └── kotlin/
│               ├── MainiOS.kt                          ← Punto de entrada KMP→iOS
│               ├── Platform.ios.kt                     ← Info del dispositivo
│               ├── core/service/
│               │   └── ServiceImplementations.kt       ← Gemini + TextRecognition iOS
│               ├── data/repository/
│               │   ├── IosAuthRepositoryImpl.kt        ← Auth (stub → Firebase real)
│               │   └── IosChatBotRepositoryImpl.kt     ← ChatBot para iOS
│               ├── di/
│               │   └── IosModule.kt                    ← Koin DI para iOS
│               └── presentation/view/
│                   ├── atoms/Camera.kt                 ← Cámara placeholder iOS
│                   ├── organisms/ChatBotOrganisms.ios.kt
│                   └── pages/AppPage.ios.kt            ← ViewModels vía Koin
└── iosApp/
    ├── iosApp/
    │   ├── iOSApp.swift      ← @main entry point SwiftUI
    │   └── ContentView.swift ← Monta ComposeUIViewController
    └── iosApp.xcodeproj/
        └── project.pbxproj   ← Configuración Xcode
```

---

## Paso 1: Clonar el repositorio en Mac

```bash
git clone https://github.com/tu-usuario/portafolio_kotlin.git
cd portafolio_kotlin
```

---

## Paso 2: Compilar el framework iOS

```bash
chmod +x build-ios-framework.sh
./build-ios-framework.sh Debug
```

O directamente con Gradle (para simulador M1/M2):

```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

El framework se genera en:
```
shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework   ← Simulador M1/M2
shared/build/bin/iosArm64/debugFramework/shared.framework            ← Dispositivo real
shared/build/bin/iosX64/debugFramework/shared.framework              ← Simulador Intel
```

---

## Paso 3: Abrir en Xcode

```bash
open iosApp/iosApp.xcodeproj
```

---

## Paso 4: Vincular el framework en Xcode

1. Selecciona el target **iosApp** en el panel izquierdo
2. Ve a **General → Frameworks, Libraries, and Embedded Content**
3. Haz clic en **+** → **Add Other → Add Files**
4. Navega a `shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework`
5. Selecciona **"Do Not Embed"** (es estático)

> Para dispositivo real usa `iosArm64/debugFramework/shared.framework`

---

## Paso 5: Verificar Build Settings

En **Build Settings** del target `iosApp`, confirma:

| Setting | Valor |
|---|---|
| `FRAMEWORK_SEARCH_PATHS` | `$(SRCROOT)/../shared/build/bin/iosSimulatorArm64/debugFramework` |
| `IPHONEOS_DEPLOYMENT_TARGET` | `16.0` |
| `SWIFT_VERSION` | `5.0` |

> Para Release: reemplaza `debugFramework` por `releaseFramework`

---

## Paso 6: Ejecutar en Simulador

Selecciona **iPhone 15 Pro** (o cualquier simulador iOS 16+) y presiona ▶.

La app mostrará la pantalla de **Login** con el tema Capibara igual que en Android y Web.

---

## Estado actual de funcionalidades iOS

| Funcionalidad | Estado | Notas |
|---|---|---|
| UI Compose Multiplatform | ✅ Funcional | Login, ChatBot, Menu, AppPage |
| Navegación (NavHost) | ✅ Funcional | Misma navegación que Android/Web |
| Tema visual (Capibara) | ✅ Funcional | Colores, tipografía, dark mode |
| Koin DI | ✅ Funcional | `commonModule + iosModule` |
| Auth (Login) | ⚠️ Stub | Acepta cualquier email/pass válido |
| ChatBot Gemini | ✅ Funcional | Usa NSURLSession → Cloud Function |
| Cámara | ⚠️ Placeholder | Requiere AVFoundation nativo |
| OCR (TextRecognition) | ⚠️ Stub | Requiere Vision framework nativo |

---

## Para activar Firebase Auth real en iOS

### 1. Crear Podfile

Crea `iosApp/Podfile`:

```ruby
platform :ios, '16.0'

target 'iosApp' do
  use_frameworks!
  pod 'FirebaseAuth'
  pod 'FirebaseCore'
end
```

### 2. Instalar pods

```bash
cd iosApp
pod install
open iosApp.xcworkspace  # Desde ahora usar .xcworkspace, NO .xcodeproj
```

### 3. Agregar GoogleService-Info.plist

- Descarga `GoogleService-Info.plist` desde Firebase Console
  (mismo proyecto que usa tu app Android)
- Arrástralo al grupo `iosApp/iosApp` en Xcode
- Marca **"Copy items if needed"**

### 4. Crear wrapper Swift para Firebase Auth

Crea `iosApp/iosApp/FirebaseAuthWrapper.swift`:

```swift
import Foundation
import FirebaseAuth
import FirebaseCore

@objc class FirebaseSetup: NSObject {
    @objc static func configure() {
        FirebaseApp.configure()
    }
}

@objc class FirebaseAuthWrapper: NSObject {
    @objc static func signIn(
        email: String,
        password: String,
        completion: @escaping (String?, String?) -> Void  // (userJson, error)
    ) {
        Auth.auth().signIn(withEmail: email, password: password) { result, error in
            if let error = error {
                completion(nil, error.localizedDescription)
                return
            }
            guard let user = result?.user else {
                completion(nil, "Usuario no encontrado")
                return
            }
            let json = """
            {"uid":"\(user.uid)","email":"\(user.email ?? "")","displayName":"\(user.displayName ?? "")"}
            """
            completion(json, nil)
        }
    }

    @objc static func signOut() throws {
        try Auth.auth().signOut()
    }

    @objc static func currentUser() -> String? {
        guard let user = Auth.auth().currentUser else { return nil }
        return """
        {"uid":"\(user.uid)","email":"\(user.email ?? "")","displayName":"\(user.displayName ?? "")"}
        """
    }
}
```

### 5. Actualizar iOSApp.swift para inicializar Firebase

```swift
import SwiftUI
import FirebaseCore  // ← Agregar

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()  // ← Agregar
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

### 6. Actualizar IosAuthRepositoryImpl.kt

Reemplaza el stub en `signInWithEmail` con:
```kotlin
// Llamar al wrapper Swift via @ObjCClass
// (Requiere configuración de interop en build.gradle.kts)
```

---

## Para activar la Cámara real en iOS (AVFoundation)

El archivo `Camera.kt` en iosMain actualmente muestra un placeholder.
Para implementación real con AVFoundation:

1. Crear `iosApp/iosApp/CameraWrapper.swift` con `AVCaptureSession`
2. Exponer vía `@objc` a Kotlin
3. Actualizar `CameraPreviewWithCapture` en `Camera.kt` para usar el wrapper

---

## Troubleshooting

### Error: "Module 'shared' not found"
→ Ejecutar `./build-ios-framework.sh` nuevamente

### Error: "Kotlin version mismatch"
→ Verificar que Xcode usa el mismo JDK: `export JAVA_HOME=$(/usr/libexec/java_home -v 17)`

### Error: "No such module 'shared'" en SwiftUI
→ Limpiar build: `Product → Clean Build Folder` (⇧⌘K) y recompilar

### La app arranca pero muestra pantalla en blanco
→ Verificar que `MainViewControllerKt.MainViewController()` en `ContentView.swift`
  coincide con el nombre de la función en `MainiOS.kt`




