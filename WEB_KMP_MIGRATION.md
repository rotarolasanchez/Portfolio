# 📄 Documento Técnico: Migración y Habilitación Web en Kotlin Multiplatform (KMP)

**Proyecto:** Portafolio Kotlin — ChatBot AI  
**Fecha:** Marzo 2026  
**Plataformas:** Android + Web (Wasm/JS)  
**Stack:** Kotlin Multiplatform · Compose Multiplatform · Koin · Firebase · Gemini API

---

## 1. Contexto del Proyecto

El proyecto estaba desarrollado originalmente en **Android nativo** con:
- Jetpack Compose
- Hilt para inyección de dependencias
- ML Kit para OCR (reconocimiento de texto en imágenes)
- Firebase Auth (Android SDK)
- Gemini API via Cloud Functions

El objetivo fue **migrar a Kotlin Multiplatform (KMP)** para que el mismo código fuente sirviera tanto para Android como para la Web (Wasm/JS), usando **Compose Multiplatform**.

---

## 2. Arquitectura KMP adoptada

```
shared/
├── commonMain/         → Código compartido (lógica, UI, ViewModels)
├── androidMain/        → Implementaciones específicas Android
├── wasmJsMain/         → Implementaciones específicas Web
├── desktopMain/        → Implementaciones básicas Desktop
└── iosMain/            → Implementaciones básicas iOS
```

### Patrón `expect/actual`

Para cada funcionalidad que difiere por plataforma se usó el patrón `expect/actual`:

```kotlin
// commonMain — contrato
expect class PlatformBitmap
expect class PlatformImageCapture

@Composable
expect fun CameraScreen(...)

@Composable
expect fun CameraPreviewWithCapture(...)
```

---

## 3. Problemas encontrados y soluciones aplicadas

### 3.1 Inyección de dependencias — De Hilt a Koin

**Problema:**  
Hilt es exclusivo de Android y no es compatible con KMP. Las anotaciones `@HiltViewModel`, `@InstallIn`, `@Module` no son reconocidas en `commonMain`.

**Solución:**  
Migración completa a **Koin**, que soporta KMP de forma nativa.

```kotlin
// ❌ Antes (Android Hilt)
@HiltViewModel
class AuthViewModel @Inject constructor(...) : ViewModel()

// ✅ Después (Koin KMP)
class AuthViewModel(
    private val signWithEmailUseCase: SignInWithEmailUseCase
) : ViewModel()
```

Los módulos se separaron por plataforma:

| Módulo | Plataforma | Archivo |
|--------|-----------|---------|
| `androidModule` | Android | `androidMain/di/AndroidModule.kt` |
| `webCommonModule` | Web/Wasm | `wasmJsMain/di/WebModule.kt` |

---

### 3.2 Firebase Auth — Interoperabilidad JS

**Problema:**  
El SDK de Firebase para Android (`com.google.firebase:firebase-auth`) no funciona en Web. Tampoco hay un SDK oficial de Firebase para Kotlin/Wasm.

**Solución:**  
Se implementó una capa de **interoperabilidad con el Firebase JS SDK** usando la anotación `@JsFun` de Kotlin/Wasm:

```kotlin
// wasmJsMain/core/interop/FirebaseInterop.kt

@JsFun("(email, password) => window.firebaseSignIn(email, password)")
external fun firebaseSignInJs(email: String, password: String): JsAny

@JsFun("() => window.firebaseGetIdToken()")
external fun firebaseGetIdTokenJs(): JsAny
```

Las funciones JavaScript correspondientes se definieron en el `index.html` como propiedades de `window`, inicializando el Firebase JS SDK directamente en el navegador.

**Flujo de autenticación en Web:**

```
Kotlin/Wasm → @JsFun → window.firebaseSignIn() → Firebase JS SDK → Firebase Auth
```

Se creó `WebAuthRepositoryImpl` que usa estas funciones interop y parsea la respuesta JSON manualmente (sin librerías de serialización para evitar problemas en Wasm):

```kotlin
class WebAuthRepositoryImpl : AuthRepository {
    override suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
        return try {
            val jsonResult = firebaseSignIn(email, password)
            val user = parseUserJson(jsonResult)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

### 3.3 Promesas JS → Coroutines Kotlin

**Problema:**  
Las funciones del Firebase JS SDK y `fetch` devuelven **Promises de JavaScript**, que no son directamente compatibles con las `suspend fun` de Kotlin/Wasm.

**Solución:**  
Se implementó un helper `awaitJsPromise` usando `suspendCancellableCoroutine`:

```kotlin
private suspend fun awaitJsPromise(promise: JsAny): String {
    return suspendCancellableCoroutine { cont ->
        thenPromise(
            promise,
            onResolve = { value -> cont.resume(value.toString()) },
            onReject  = { error -> cont.resumeWithException(Exception(error.toString())) }
        )
    }
}
```

Esto permite usar las APIs de JS como si fueran funciones suspend normales:

```kotlin
// Uso transparente como suspend fun
val token = firebaseGetIdToken()         // internamente es una Promise JS
val response = fetchWithAuth(url, token, body)  // también Promise JS
```

---

### 3.4 ML Kit OCR → Gemini Multimodal en Web

**Problema:**  
**ML Kit** (Google) es exclusivo de Android/iOS. No tiene implementación para Web. En Android el flujo era:

```
Imagen → ML Kit OCR → texto → Gemini API → respuesta
```

**Solución:**  
En Web se unificó el proceso en una **sola llamada multimodal a Gemini**, enviando la imagen como base64 directamente a la Cloud Function, que a su vez la procesa con Gemini 1.5 (que hace OCR + análisis en un solo paso):

```kotlin
// wasmJsMain — GeminiCloudServiceImpl
override suspend fun analyzeImage(bitmap: PlatformBitmap): String {
    val imageData = bitmap.imageData
    if (imageData != null && imageData.startsWith("data:image")) {
        val base64Data = imageData.substringAfter(";base64,")
        val mimeType   = imageData.substringAfter("data:").substringBefore(";base64,")

        return callCloudFunctionWithImage(
            prompt = "1. Extrae el texto (OCR).\n2. Analiza el contenido paso a paso.",
            base64Data, mimeType
        )
    }
    return "Error: No se pudo procesar la imagen."
}
```

| Plataforma | OCR | Análisis IA |
|------------|-----|-------------|
| Android | ML Kit (on-device) | Gemini Cloud Function |
| Web | Gemini 1.5 (multimodal) | Gemini Cloud Function (mismo paso) |

---

### 3.5 Cámara — Funcionalidad diferenciada por plataforma

**Problema:**  
`CameraX` (Android) no existe en Web. Los composables de cámara (`ProcessCameraProvider`, `PreviewView`, `ImageCapture`) son exclusivos de Android.

**Solución:**  
Se separó la implementación con `expect/actual`:

- **Android:** `CameraX` con `AndroidView` y `ProcessCameraProvider`
- **Web:** Selector de archivos del navegador (`input[type=file]`) via `pickImageFile()`, que usa la File API del navegador

```kotlin
// wasmJsMain — CameraScreen para Web
@Composable
actual fun CameraScreen(...) {
    // En vez de cámara en vivo, ofrece selector de imágenes
    Button(onClick = {
        scope.launch {
            val imageBase64 = pickImageFile() // File API del navegador
            if (imageBase64 != null) {
                onImageCaptured(PlatformBitmap(imageBase64))
            }
        }
    }) {
        Text("Seleccionar Imagen")
    }
}
```

---

### 3.6 Construcción de JSON para imágenes Base64 en Wasm

**Problema:**  
Al construir strings JSON con imágenes en base64 (que pueden ser varios MB) directamente en Kotlin/Wasm, se producían errores de memoria y timeouts.

**Solución:**  
Se delegó la construcción del JSON al lado de JavaScript usando `JSON.stringify`, evitando manipular strings enormes en el heap de Wasm:

```kotlin
@JsFun("(url, token, message, imageBase64, mimeType) => window.fetchImageWithAuth(url, token, message, imageBase64, mimeType)")
external fun fetchImageWithAuthJs(...): JsAny
```

La función JavaScript `window.fetchImageWithAuth` construye el JSON con `JSON.stringify` nativo del navegador, que es más eficiente para datos grandes.

---

### 3.7 ViewModels — Provisión multiplataforma

**Problema:**  
Hilt provee ViewModels automáticamente con `hiltViewModel()`. En KMP esto no existe en `commonMain`.

**Solución:**  
Se usó el patrón `expect/actual` para la provisión de ViewModels:

```kotlin
// commonMain — contrato
@Composable
expect fun provideAuthViewModel(): AuthViewModel

@Composable
expect fun provideChatBotViewModel(): ChatBotViewModel

@Composable
expect fun provideMenuViewModel(): MenuViewModel
```

```kotlin
// wasmJsMain — implementación con Koin
@Composable
actual fun provideAuthViewModel(): AuthViewModel {
    return GlobalContext.get().get()  // Koin
}
```

```kotlin
// androidMain — implementación con Koin
@Composable
actual fun provideAuthViewModel(): AuthViewModel {
    return koinViewModel()  // koin-androidx-compose
}
```

---

### 3.8 Imágenes y recursos — Compose Resources

**Problema:**  
`R.drawable.*` de Android no existe en KMP. Los recursos de tipo `@android:color/white` en archivos vectoriales causaban crashes en Compose Multiplatform.

**Solución:**  
- Todos los drawables se migraron a `commonMain/composeResources/drawable/`
- Se referenciaron con `Res.drawable.*` (API de Compose Multiplatform Resources)
- Los vectores con referencias a colores de sistema Android se convirtieron a valores hardcodeados compatibles

```kotlin
// ❌ Antes
R.drawable.outline_login_24

// ✅ Después
Res.drawable.outline_login_24
```

---

### 3.9 Responsividad Web

**Problema:**  
Los layouts de Android no se adaptaban bien a pantallas de escritorio/web (formularios muy anchos, elementos desbordados).

**Solución:**  
Se creó el composable `ResponsiveContainer` en `commonMain` que limita el ancho máximo y aplica scroll vertical:

```kotlin
@Composable
fun ResponsiveContainer(
    maxWidth: Dp = 600.dp,
    enableScroll: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            content = content
        )
    }
}
```

El formulario de login y otras pantallas lo usan para verse bien en cualquier tamaño de pantalla.

---

### 3.10 Cloud Function — Soporte multimodal

**Problema:**  
La Cloud Function original solo aceptaba texto plano. Para Web necesitaba recibir imágenes base64.

**Solución:**  
Se actualizó la Cloud Function en Firebase (`functions/index.js`) para aceptar el campo `imageBase64` y `mimeType`, construyendo la petición multimodal hacia la API de Gemini:

```javascript
// functions/index.js
if (imageBase64 && mimeType) {
    // Petición multimodal: texto + imagen
    parts = [
        { text: message },
        { inline_data: { mime_type: mimeType, data: imageBase64 } }
    ];
} else {
    // Petición solo de texto
    parts = [{ text: message }];
}
```

Se hizo `firebase deploy --only functions --force` para aplicar los cambios.

---

### 3.11 Evento de click en botones Web

**Problema:**  
El área de click de los botones en Web solo respondía en la mitad inferior del botón. Esto era causado por overlapping de composables internos que interceptaban los eventos de pointer.

**Solución:**  
Se ajustó el composable `ChatBotButton` eliminando modificadores de `pointerInput` en capas interiores que bloqueaban la propagación del evento hacia el `Button` padre, asegurando que el `Modifier` de click estuviera únicamente en el elemento raíz.

---

### 3.12 Try-catch alrededor de Composables

**Problema:**  
Compose Multiplatform no permite `try-catch` alrededor de invocaciones de funciones `@Composable`.

```
e: Try catch is not supported around composable function invocations.
```

**Solución:**  
Se movió el manejo de errores fuera del bloque composable, usando `runCatching` en lambdas no-composable (callbacks de navegación):

```kotlin
// ✅ Correcto — runCatching en lambda no-composable
onLoginClick = { code, password ->
    runCatching {
        authViewModel.signInWithEmail(code, password)
    }
}
```

---

## 4. Flujo completo en Web

```
index.html
  └── Firebase JS SDK inicializado (window.firebaseSignIn, etc.)
  └── Compose Wasm canvas (ComposeTarget)

Main.kt (wasmJsMain)
  └── startKoin { modules(webCommonModule) }
  └── CanvasBasedWindow { App() }

App() (commonMain)
  └── FeatureUITheme { NavigationMain() }

NavigationMain() → NavHost
  ├── "login"   → LoginPage  → LoginTemplate  → LoginContent
  ├── "menu"    → MenuPage   → MenuTemplate
  └── "chatbot" → ChatBotPage → ChatBotTemplate → ChatScreen / CameraScreen
```

---

## 5. Resumen de cambios por capa

| Capa | Cambio |
|------|--------|
| **DI** | Hilt → Koin KMP |
| **Auth** | Firebase Android SDK → Firebase JS SDK via `@JsFun` interop |
| **OCR** | ML Kit (Android only) → Gemini multimodal (Web) |
| **Cámara** | CameraX → File API del navegador (`input[type=file]`) |
| **ViewModels** | `hiltViewModel()` → `expect/actual` + Koin |
| **Recursos** | `R.drawable.*` → `Res.drawable.*` (Compose Resources) |
| **Promesas JS** | Ninguna → `awaitJsPromise` con `suspendCancellableCoroutine` |
| **JSON Base64** | Kotlin string builder → `JSON.stringify` en JS |
| **Responsividad** | Android layouts → `ResponsiveContainer` multiplataforma |
| **Cloud Function** | Solo texto → Texto + imagen multimodal |
| **Tema** | Android `MaterialTheme` → `FeatureUITheme` en `commonMain` |

---

## 6. Tests — Compatibilidad KMP

Los tests unitarios con `kotlin.test` y `kotlinx-coroutines-test` son **100% compatibles con KMP** y se ejecutan en `commonTest`, sin cambios:

```kotlin
// commonTest — funciona en Android, Web, Desktop
class SignUpUseCaseTest {
    @Test
    fun `registro exitoso retorna usuario con datos correctos`() = runTest {
        val fakeRepo = FakeAuthRepository(
            signUpResult = Result.success(expectedUser)
        )
        val useCase = SignUpUseCase(fakeRepo)
        val result = useCase("nuevo@test.com", "pass1234", "Nuevo Usuario")
        assertTrue(result.isSuccess)
    }
}
```

Los `FakeRepository` en `commonTest/utils/` simulan las implementaciones reales permitiendo TDD sin dependencias de plataforma.

---

## 7. Comandos de despliegue

```bash
# Compilar Web (Wasm)
./gradlew :shared:wasmJsBrowserDevelopmentRun

# Compilar Android
./gradlew :app:assembleDebug

# Deploy Cloud Functions
firebase deploy --only functions --force

# Deploy Web (Firebase Hosting)
firebase deploy --only hosting
```

---

*Documento generado a partir del análisis del código fuente del proyecto `portafolio_kotlin`.*

