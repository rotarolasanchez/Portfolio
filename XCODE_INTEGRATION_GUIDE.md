# 📱 Guía Rápida - Integración del Framework iOS en Xcode

## ✅ Framework Compilado

```
📍 Ubicación: shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework
📦 Tamaño: [Framework compilado exitosamente]
🎯 Arquitectura: arm64 (compatible con simulador M1/M2/M3)
```

---

## 🔧 Pasos para Integrar en Xcode

### 1️⃣ Abrir Proyecto en Xcode

```bash
cd /Users/user295220/dev/Portfolio
open iosApp/iosApp.xcodeproj
```

### 2️⃣ Verificar la Configuración Básica

En Xcode:
- ✅ Selecciona el target **`iosApp`** en el panel izquierdo
- ✅ Ve a la pestaña **Build Settings**
- ✅ Busca **iOS Deployment Target** → Debe ser **16.0 o superior**
- ✅ Verifica **Supported Platforms** → Incluye **iphonesimulator**

### 3️⃣ Agregar el Framework

**Opción A: Drag & Drop (Más fácil)**

1. En Finder, navega a: `shared/build/bin/iosSimulatorArm64/debugFramework/`
2. Arrastra `shared.framework` a Xcode dentro del grupo `iosApp`
3. En el popup que aparece:
   - ✅ Marca **Copy items if needed**
   - ✅ Marca **Create groups**
   - ✅ Selecciona target **iosApp**
   - ✅ Click **Finish**

**Opción B: Configuración Manual**

1. En Xcode, selecciona target `iosApp`
2. Ve a **General** → **Frameworks, Libraries, and Embedded Content**
3. Click en **+** → **Add Other...**
4. Navega a `shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework`
5. Selecciona el framework
6. En **Embed** selecciona **Do Not Embed** (es estático)

### 4️⃣ Configurar Build Settings

Después de agregar el framework:

1. Ve a **Build Settings**
2. Busca **Framework Search Paths** → Agrega:
   ```
   $(PROJECT_DIR)/shared/build/bin/iosSimulatorArm64/debugFramework
   ```

3. Busca **Other Linker Flags** → Agrega:
   ```
   -framework shared
   ```

### 5️⃣ Limpiar y Compilar

```bash
# En Xcode o terminal
⌘Shift+K  # Limpiar build

⌘B        # Build (compilar)
```

Si hay errores, prueba:
```bash
# Desde terminal
cd /Users/user295220/dev/Portfolio/iosApp
xcodebuild clean -scheme iosApp
xcodebuild build -scheme iosApp -destination 'generic/platform=iOS Simulator'
```

### 6️⃣ Ejecutar en Simulador

```bash
⌘R  # Run en simulador (requiere un simulador activo)
```

O desde terminal:
```bash
open -a Simulator
# Espera a que abra, luego en Xcode: ⌘R
```

---

## 🎯 Simuladores Disponibles

```bash
# Listar simuladores
xcrun simctl list devices

# Crear nuevo simulador si necesitas
xcrun simctl create "iPhone 15 Pro Simulator" com.apple.CoreSimulator.SimDeviceType.iPhone-15-Pro com.apple.CoreSimulator.SimRuntime.iOS-18-2
```

Para máquina en la nube (macOS Cloud):
- ✅ Usa simulador **arm64** (M-series)
- ✅ Arquitectura: **iosSimulatorArm64**

---

## ✅ Checklist de Compilación Exitosa

- [ ] Framework `shared.framework` existe en `iosSimulatorArm64/debugFramework/`
- [ ] Framework agregado a **Build Phases** → **Link Binary With Libraries**
- [ ] Framework embedding es **Do Not Embed**
- [ ] **Framework Search Paths** configurado
- [ ] **iOS Deployment Target** ≥ 16.0
- [ ] Build Settings clean y sin errores (⌘B)
- [ ] Simulador seleccionado
- [ ] App ejecutable en simulador (⌘R)

---

## 🐛 Troubleshooting Común

### Error: "Module 'shared' not found"
```bash
# Solución 1: Limpiar y recompilar
cd /Users/user295220/dev/Portfolio
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

### Error: "Build of target failed with error"
```bash
# Solución: Limpiar caché de Xcode
rm -rf ~/Library/Developer/Xcode/DerivedData/
```

### Error: "Architecture mismatch"
- Verifica que el simulador es **arm64** (no x86_64)
- En Xcode → Product → Destination → Elige "iPhone 15 Simulator" o similar

### La app abre pero muestra pantalla en blanco
- Verifica `ContentView.swift` llama a `MainViewControllerKt.MainViewController()`
- Verifica que Koin se inicializa en `MainiOS.kt`

---

## 📊 Próximas Compilaciones

Cuando hagas cambios en `shared/`:

```bash
# Recompilar el framework (sin cambios en build.gradle.kts)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Para dispositivo real
./gradlew :shared:linkDebugFrameworkIosArm64

# Release
./gradlew :shared:linkReleaseFrameworkIosSimulatorArm64
```

Luego en Xcode:
```
⌘B → Compila con el nuevo framework
```

---

## 🚀 Configuración Futura (HTTP Real para Gemini)

Cuando implementes HTTP real, necesitarás:

1. **Crear wrapper Swift** para URLSession
2. **Agregar GoogleService-Info.plist** para Firebase
3. **Exportar a Kotlin** vía `@objc`

Ejemplo:
```swift
// iosApp/iosApp/GeminiWrapper.swift
import Foundation

@objc class GeminiAPIClient: NSObject {
    @objc static func callGeminiAPI(
        prompt: String,
        completion: @escaping (String?, Error?) -> Void
    ) {
        // Implementación con URLSession + Gemini API Key
    }
}
```

---

**¡Ahora estás listo para compilar y ejecutar en Xcode! 🎉**

Cualquier problema, revisa este documento o consulta `IOS_BUILD_FIXES.md` para detalles técnicos.

