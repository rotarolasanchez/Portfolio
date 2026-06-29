# 📐 ARCHITECTURE — CapibaraFamily Portfolio KMP

> Documento de referencia de arquitectura real del proyecto.
> Última actualización: April 2026 · Kotlin 2.1.20 · CMP 1.8.x

---

## 📦 Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.1.20 |
| UI | Compose Multiplatform (CMP) |
| DI | Koin (NO Hilt/Dagger) |
| Async | Kotlin Coroutines + StateFlow |
| ViewModel | `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:2.8.4` (KMP) |
| Backend | Firebase Cloud Functions (Node.js) |
| AI | Gemini API (vía Cloud Function `askGemini`) |
| CI/CD | GitHub Actions |

---

## 🗂️ Estructura de módulos

```
portafolio_kotlin/
├── app/                        # Android entry point (Activity + Koin init)
├── shared/                     # Módulo KMP — fuente de verdad
│   └── src/
│       ├── commonMain/         # Código 100% compartido
│       ├── androidMain/        # Implementaciones Android
│       ├── iosMain/            # Implementaciones iOS (Kotlin/Native)
│       ├── wasmJsMain/         # Implementaciones Web (WasmJs)
│       └── desktopMain/        # Implementaciones Desktop (experimental)
├── functions/                  # Firebase Cloud Functions (Node.js)
└── iosApp/                     # Proyecto Xcode que consume shared.framework
```

---

## 🧱 Arquitectura por capas (Clean Architecture)

```
┌──────────────────────────────────────────────┐
│              PRESENTATION                     │
│  ViewModels · UiState · Composables           │
│  (commonMain — 100% compartido)               │
├──────────────────────────────────────────────┤
│              DOMAIN                           │
│  UseCases · Repository Interfaces · Models    │
│  (commonMain — 100% compartido)               │
├──────────────────────────────────────────────┤
│              DATA                             │
│  Repository Impls · DataSources · Services    │
│  (plataforma-específico)                      │
└──────────────────────────────────────────────┘
```

### Capas en commonMain

```
commonMain/kotlin/
├── App.kt                           # Entry point Compose (@Composable fun App())
├── core/
│   ├── model/PlatformBitmap.kt      # expect class (cada plataforma implementa)
│   ├── service/
│   │   ├── GeminiCloudService.kt    # interface: analyzeImage, solveProblem, continueChat
│   │   └── TextRecognitionService.kt# interface: recognizeText(ByteArray)
│   ├── storage/CredentialsStorage.kt# interface: save/load/clear credentials
│   └── utils/
│       ├── Constans.kt              # GEMINI_FUNCTION_URL, GEMINI_DIRECT_URL
│       └── Functions.kt             # utilidades comunes
├── data/
│   └── repositories/               # AuthRepository, ChatBotRepository (interfaces)
├── domain/
│   ├── model/
│   │   ├── UserModel.kt             # id, email, userName, userCode
│   │   ├── ChatBot.kt               # ChatBotMessage(text, isFromUser)
│   │   └── RequestState.kt          # sealed: Idle/Loading/Success/Error
│   ├── repositories/
│   │   ├── AuthRepository.kt        # signInWithEmail, signOut, getCurrentUser...
│   │   └── ChatBotRepository.kt     # analyzeImage, solveProblem, continueChat
│   └── usecases/
│       ├── SignInWithEmailUseCase.kt
│       ├── AnalyzeImageUseCase.kt
│       ├── SolveProblemUseCase.kt
│       └── ContinueChatUseCase.kt
├── di/
│   └── CommonModule.kt              # ViewModels (factory) + UseCases (single)
└── presentation/
    ├── state/
    │   ├── AuthUiState.kt
    │   ├── ChatBotUiState.kt
    │   └── MenuUiState.kt
    ├── viewmodels/
    │   ├── AuthViewModel.kt         # : ViewModel() + viewModelScope
    │   ├── ChatBotViewModel.kt
    │   └── MenuViewModel.kt
    └── view/
        ├── atoms/       → moléculas mínimas reutilizables
        ├── molecules/   → combinación de átomos
        ├── organisms/   → LoginContent, ChatBotOrganisms, MenuOrganisms
        ├── templates/   → LoginTemplate, ChatBotTemplate, MenuTemplate
        └── pages/
            ├── AppPage.kt           # vacío — NavigationMain() es expect
            ├── ViewModelProviders.kt# expect fun provideXxxViewModel() + NavigationMain()
            ├── LoginPage.kt
            ├── ChatBotPage.kt
            └── MenuPage.kt
```

---

## 🤖 Patrón expect/actual

### Declaración (commonMain)
```kotlin
// ViewModelProviders.kt
@Composable expect fun provideAuthViewModel(): AuthViewModel
@Composable expect fun provideChatBotViewModel(): ChatBotViewModel
@Composable expect fun provideMenuViewModel(): MenuViewModel
@Composable expect fun NavigationMain()
```

### Android (`androidMain`)
```kotlin
@Composable actual fun provideAuthViewModel(): AuthViewModel = koinViewModel()
@Composable actual fun NavigationMain() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginPage(...) }
        composable("menu")  { MenuPage(...) }
        composable("chatbot") { ChatBotPage(...) }
    }
}
```

### iOS (`iosMain`)
```kotlin
@Composable actual fun provideAuthViewModel(): AuthViewModel =
    remember { IosViewModelHolder.authViewModel!! }
@Composable actual fun NavigationMain() {
    var currentScreen by remember { mutableStateOf("login") }
    when (currentScreen) {
        "login"   -> LoginPage(onLoginSuccess = { currentScreen = "menu" })
        "menu"    -> MenuPage(...)
        "chatbot" -> ChatBotPage(...)
    }
}
```
> ⚠️ iOS NO usa `navigation-compose` — crash en versión alpha con `_NSCFNumber`

### Web (`wasmJsMain`)
```kotlin
@Composable actual fun provideAuthViewModel(): AuthViewModel =
    GlobalContext.get().get()
@Composable actual fun NavigationMain() {
    var currentScreen by remember { mutableStateOf("login") }
    // state machine igual a iOS
}
```

---

## 🤖 DI — Koin por plataforma

### CommonModule (todos)
```kotlin
val commonModule = module {
    single { AnalyzeImageUseCase(get()) }
    single { SolveProblemUseCase(get()) }
    single { ContinueChatUseCase(get()) }
    single { SignInWithEmailUseCase(get()) }
    factory { ChatBotViewModel(get(), get(), get()) }
    factory { AuthViewModel(get(), get()) }
    factory { MenuViewModel() }
}
```

### Android — `androidModule`
```kotlin
single { FirebaseAuth.getInstance() }
single { FirebaseFirestore.getInstance() }
single { AuthDataSource() }
single<TextRecognitionService>  { TextRecognitionServiceImpl() }   // ML Kit
single<GeminiCloudService>      { GeminiCloudServiceImpl() }       // OkHttp
single<AuthRepository>          { AuthRepositoryImpl(get()) }
single<ChatBotRepository>       { ChatBotRepositoryImpl(get(), get()) }
single<CredentialsStorage>      { AndroidCredentialsStorage(get()) }
```
Inicio en `Application.onCreate()`:
```kotlin
startKoin { androidContext(this@App); modules(commonModule, androidModule) }
```

### iOS — `iosModule`
```kotlin
single<TextRecognitionService> { TextRecognitionServiceImpl() }   // stub Vision
single<GeminiCloudService>     { GeminiCloudServiceImpl() }       // Ktor Darwin
single<AuthRepository>         { IosAuthRepositoryImpl() }
single<ChatBotRepository>      { IosChatBotRepositoryImpl(get(), get()) }
single<CredentialsStorage>     { IosCredentialsStorage() }        // NSUserDefaults
```
Inicio en `MainiOS.kt → MainViewController()`:
```kotlin
fun MainViewController(): UIViewController {
    if (!IosViewModelHolder.isInitialized()) {
        val koin = startKoin { modules(commonModule, iosModule) }.koin
        IosViewModelHolder.authViewModel    = koin.get()
        IosViewModelHolder.chatBotViewModel = koin.get()
        IosViewModelHolder.menuViewModel    = koin.get()
        IosViewModelHolder.preloadBitmaps()
    }
    return ComposeUIViewController { App() }
}
```

### Web — `webModule`
```kotlin
single<TextRecognitionService> { TextRecognitionServiceImpl() }
single<GeminiCloudService>     { GeminiCloudServiceImpl() }       // Fetch API JS
single<AuthRepository>         { WebAuthRepositoryImpl() }        // Firebase JS SDK
single<ChatBotRepository>      { WebChatBotRepositoryImpl(get(), get()) }
single<CredentialsStorage>     { WebCredentialsStorage() }        // localStorage
single { ChatBotViewModel(get(), get(), get()) }
single { AuthViewModel(get(), get()) }
single { MenuViewModel() }
```

---

## 📱 Android

```
androidMain/kotlin/
├── core/
│   ├── service/
│   │   ├── GeminiCloudService.kt       # OkHttp → Cloud Function (Bearer token)
│   │   └── TextRecognitionServiceImpl.kt# ML Kit Text Recognition
│   ├── storage/AndroidCredentialsStorage.kt # SharedPreferences
│   └── utils/ImageUtils.kt
├── data/
│   ├── datasources/AuthDataSource.kt   # FirebaseAuth.signInWithEmailAndPassword()
│   └── repository/
│       ├── AuthRepositoryImpl.kt       # Firebase Auth real
│       └── ChatRepositoryImpl.kt       # Gemini vía Cloud Function
├── di/
│   ├── AndroidModule.kt
│   ├── ChatBotModule.kt
│   └── UseCaseModule.kt
└── presentation/view/
    ├── atoms/
    │   ├── Camera.kt                   # CameraX (solo Android)
    │   ├── SafeIconPainter.android.kt  # painterResource() directo
    │   └── SafeImage.android.kt        # painterResource() directo
    ├── organisms/ChatBotOrganisms.android.kt
    └── pages/
        ├── AppPage.android.kt          # NavHost + koinViewModel()
        └── ChatBotPageAndroid.kt
```

### Flujo Auth Android
```
LoginPage → AuthViewModel.signInWithEmail()
         → SignInWithEmailUseCase
         → AuthRepositoryImpl
         → AuthDataSource.signInWithEmail()
         → FirebaseAuth.signInWithEmailAndPassword()
         → FirebaseUser → UserModel
```

### Flujo Chat Android
```
ChatBotPage → ChatBotViewModel.sendMessage()
           → ContinueChatUseCase
           → ChatBotRepositoryImpl
           → GeminiCloudServiceImpl.continueChat()
           → OkHttp POST GEMINI_FUNCTION_URL
           → Header: Authorization: Bearer <Firebase ID Token>
           → Body: { message, conversationHistory }
```

---

## 🍎 iOS

```
iosMain/kotlin/
├── MainiOS.kt                           # MainViewController() — entry point
├── core/
│   ├── model/PlatformBitmap.kt          # actual: wraps ImageBitmap
│   ├── service/ServiceImplementations.kt
│   │   ├── TextRecognitionServiceImpl   # stub (Vision pendiente)
│   │   └── GeminiCloudServiceImpl       # Ktor Darwin → Cloud Function
│   └── storage/IosCredentialsStorage.kt # NSUserDefaults
├── data/repository/
│   ├── IosAuthRepositoryImpl.kt         # guarda email+pass en IosViewModelHolder
│   └── IosChatBotRepositoryImpl.kt      # delega a GeminiCloudServiceImpl
├── di/
│   ├── IosModule.kt                     # Koin iosModule
│   └── IosViewModelHolder.kt            # singleton de ViewModels + credenciales
└── presentation/view/
    ├── atoms/
    │   ├── SafeIconPainter.ios.kt       # Material Icons (NO painterResource XML)
    │   └── SafeImage.ios.kt             # preloadedBitmaps (PNG precargados)
    ├── organisms/ChatBotOrganisms.ios.kt
    └── pages/AppPage.ios.kt             # state machine + IosViewModelHolder
```

### IosViewModelHolder
```kotlin
object IosViewModelHolder {
    var authViewModel:    AuthViewModel?    = null
    var chatBotViewModel: ChatBotViewModel? = null
    var menuViewModel:    MenuViewModel?    = null
    var firebaseIdToken:  String?           = null
    var savedEmail:       String?           = null   // para re-auth
    var savedPassword:    String?           = null   // para re-auth
    val preloadedBitmaps: MutableMap<String, ImageBitmap> = mutableMapOf()
    fun isInitialized(): Boolean = authViewModel != null
    fun preloadBitmaps()  // NSBundle → NSData → ByteArray → SkiaImage
}
```

### Flujo Auth iOS
```
LoginPage → AuthViewModel.signInWithEmail()
         → SignInWithEmailUseCase
         → IosAuthRepositoryImpl.signInWithEmail()
         → Ktor POST Firebase Auth REST API (?key=FIREBASE_WEB_API_KEY)
         → Obtiene idToken real → guarda en IosViewModelHolder.firebaseIdToken
         → Retorna UserModel
```
> `FIREBASE_WEB_API_KEY` se resuelve en build time (ver sección ⚙️ más abajo).
> Si está vacía o en blanco, usa modo fallback email+password automáticamente.

### Flujo Chat iOS
```
ChatBotPage → ChatBotViewModel.sendMessage()
           → ContinueChatUseCase
           → IosChatBotRepositoryImpl
           → GeminiCloudServiceImpl.continueChat()  [Ktor Darwin]
           → POST GEMINI_FUNCTION_URL
           → Body SIEMPRE: { message, email, password, conversationHistory }
           → Header opcional: Authorization: Bearer <token>  (si login obtuvo token)
           → Cloud Function: intenta Bearer primero → si falla usa email+password del body
```

> **¿Por qué siempre email+password?**
> iOS KMP no tiene Firebase SDK. Si solo se envía Bearer y este expira/es inválido,
> no hay forma de renovarlo automáticamente. Incluyendo email+password como respaldo
> en el mismo body, el servidor siempre puede autenticar sin retry desde el cliente.

### ⚙️ Configuración de FIREBASE_WEB_API_KEY (iOS)

**No se necesita configuración manual.** El sistema la extrae automáticamente
del `app/google-services.json` que ya existe en el proyecto Android.

#### Prioridad de resolución (en `shared/build.gradle.kts`)
```
1. local.properties        FIREBASE_WEB_API_KEY=<valor>   ← override manual
2. Variable de entorno      FIREBASE_WEB_API_KEY           ← CI/CD
3. app/google-services.json "current_key"                  ← AUTO (nuevo)
4. ""                       → modo fallback email+password ← sin configuración
```

#### Cómo funciona la extracción automática
```kotlin
// shared/build.gradle.kts
fun extractKeyFromGoogleServices(): String? {
    val gsFile = rootProject.file("app/google-services.json")
    if (!gsFile.exists()) return null
    val regex = Regex(""""current_key"\s*:\s*"([^"]+)"""")
    return regex.find(gsFile.readText())?.groupValues?.get(1)
}

val firebaseWebApiKey: String =
    localProps.getProperty("FIREBASE_WEB_API_KEY")?.takeIf { it.isNotBlank() }
        ?: System.getenv("FIREBASE_WEB_API_KEY")?.takeIf { it.isNotBlank() }
        ?: extractKeyFromGoogleServices()   // ← lee google-services.json
        ?: ""
```

Al compilar verás en el log de Gradle:
```
✅ shared: FIREBASE_WEB_API_KEY configurada (AIzaSy...)
```
o
```
⚠️  shared: FIREBASE_WEB_API_KEY no encontrada — iOS usará modo fallback
```

#### Dónde vive la clave en google-services.json
```json
{
  "client": [{
    "api_key": [{
      "current_key": "AIzaSy..."   ← este valor se extrae automáticamente
    }]
  }]
}
```

Una vez configurada (automática o manual), iOS obtiene un Bearer token real
igual que Android, sin depender del fallback email+password.

### ✅ Solución al error 401 (resuelto)

**Síntoma previo:**
```
Error del servidor (401): {"error":"No se proporcionó token de autenticación"}
```

**Causa raíz:** iOS enviaba Bearer token OR email+password, nunca ambos.
Si el Bearer era inválido, no había fallback disponible en el mismo request.

**Solución aplicada:**

| Capa | Cambio |
|---|---|
| `ServiceImplementations.kt` (iOS) | Siempre incluye `email+password` en el body; agrega Bearer como header opcional |
| `functions/index.js` (Cloud Function v5) | Intenta Bearer primero; si falla, usa `email+password` del mismo body |
| `WEB_API_KEY` (Secret Manager) | Renombrado desde `FIREBASE_WEB_API_KEY` (prefijo `FIREBASE_` reservado) |

```
Antes (roto):
  iOS → POST { message }  +  Bearer <token>
  Si Bearer inválido → 401 sin fallback ❌

Después (funcional):
  iOS → POST { message, email, password }  +  Bearer <token> (opcional)
  Si Bearer inválido → servidor usa email+password del body ✅
  Si sin Bearer → servidor usa email+password del body ✅
```


---

## 🌐 Web (WasmJs)

```
wasmJsMain/kotlin/
├── Main.kt                              # entry point: startKoinForWeb() + renderComposable
├── core/
│   ├── interop/FirebaseInterop.kt       # @JsFun bridges → window.firebaseXxx()
│   ├── model/PlatformBitmap.kt          # actual: wraps ImageBitmap (base64)
│   ├── service/ServiceImplementations.kt
│   │   ├── TextRecognitionServiceImpl   # stub (OCR no disponible en web)
│   │   └── GeminiCloudServiceImpl       # Fetch API JS → Cloud Function
│   ├── storage/WebCredentialsStorage.kt # localStorage
│   └── utils/WebImageUtils.kt
├── data/repository/
│   ├── WebAuthRepositoryImpl.kt         # Firebase Auth JS SDK
│   └── WebChatBotRepositoryImpl.kt      # Gemini vía Cloud Function
└── di/
    ├── WebModule.kt                     # Koin webModule (ViewModels como single)
    └── WebChatBotRepositoryImpl.kt
```

### Interop JS (WasmJs)
```kotlin
// Patrón obligatorio en Kotlin/WasmJs 2.1.20:
@JsFun("(email, password) => window.firebaseSignIn(email, password)")
external fun firebaseSignInJs(email: String, password: String): JsAny
// ⚠️ Return type nullable siempre JsAny? (NO JsString?)
// ⚠️ NO pasar lambdas Kotlin como parámetros en external fun
// ⚠️ Usar polling con delay() para Promises (no callbacks)
```

### Flujo Auth Web
```
LoginPage → AuthViewModel.signInWithEmail()
         → SignInWithEmailUseCase
         → WebAuthRepositoryImpl.signInWithEmail()
         → firebaseSignIn() [Firebase JS SDK]
         → Obtiene idToken
         → Retorna UserModel
```

### Flujo Chat Web
```
ChatBotPage → ChatBotViewModel.sendMessage()
           → ContinueChatUseCase
           → WebChatBotRepositoryImpl
           → GeminiCloudServiceImpl.continueChat()
           → fetchWithAuth(url, idToken, body) [Fetch API JS]
           → POST GEMINI_FUNCTION_URL
           → Header: Authorization: Bearer <Firebase ID Token>
```

---

## ☁️ Backend — Cloud Function `askGemini`

**URL**: `https://askgemini-766ctyoljq-uc.a.run.app`
**Runtime**: Node.js · Firebase Functions v2
**Archivo**: `functions/index.js`

### Autenticación (v5) — Bearer con fallback en el mismo request
```javascript
if (authHeader) {
    try {
        // Intenta verificar Bearer token (Android / Web / iOS con token válido)
        await admin.auth().verifyIdToken(token);
    } catch (bearerError) {
        // Bearer inválido o expirado → usa email+password del body (iOS fallback)
        if (email && password) {
            await firebaseSignInRest(email, password, webApiKey.value());
        } else {
            return res.status(401).json({ error: "No se proporcionó token de autenticación" });
        }
    }
} else if (email && password) {
    // Sin Bearer → autentica solo con email+password (iOS sin token)
    await firebaseSignInRest(email, password, webApiKey.value());
} else {
    return res.status(401).json({ error: "No se proporcionó token de autenticación" });
}
```

### Secretos requeridos (Secret Manager)
| Secreto | Descripción |
|---|---|
| `GEMINI_API_KEY` | API Key de Google AI Studio |
| `WEB_API_KEY` | Web API Key del proyecto Firebase (para auth iOS email+password) |

> ⚠️ Firebase no permite secretos con prefijo `FIREBASE_`. Por eso se llama `WEB_API_KEY`.

### Request body
```json
{
  "message": "texto del usuario",
  "conversationHistory": [{ "text": "...", "isUser": true }],
  "email": "...",         // iOS solamente
  "password": "...",      // iOS solamente
  "imageBase64": "...",   // opcional (análisis de imagen)
  "imageMimeType": "..."  // opcional
}
```

### Response
```json
{ "response": "texto de Gemini", "success": true }
```

### Modelos Gemini (fallback en cascada)
`gemini-2.0-flash` → `gemini-1.5-flash` → `gemini-1.5-pro`

---

## 🔐 Auth por plataforma

| Plataforma | Cómo obtiene token | Request a Cloud Function |
|---|---|---|
| **Android** | Firebase Auth SDK → `getIdToken()` automático | Solo `Bearer <token>` |
| **Web** | Firebase Auth JS SDK → token automático | Solo `Bearer <token>` |
| **iOS** | Firebase Auth REST API (Ktor) con `FIREBASE_WEB_API_KEY` | `email+password` en body **siempre** + `Bearer` si disponible |

### ¿Por qué iOS es diferente?

No existe Firebase SDK para Kotlin Multiplatform en iOS. Esto tiene dos consecuencias:

1. **El token no se renueva automáticamente** — si expira, no hay SDK que lo refresque
2. **El token es opcional** — iOS puede autenticarse solo con email+password en el body

Por eso iOS siempre incluye `email+password` en el body como garantía, y agrega el
Bearer token como optimización cuando lo tiene. La Cloud Function intenta Bearer primero;
si falla, usa email+password del mismo body. **No se necesita retry desde el cliente.**

```
Android/Web → Cloud Function:
  Header: Authorization: Bearer <valid_token>
  Body:   { message, conversationHistory }

iOS → Cloud Function:
  Header: Authorization: Bearer <token>  ← opcional, si se obtuvo en login
  Body:   { message, email, password, conversationHistory }  ← siempre presente

Cloud Function:
  1. ¿Tiene Bearer? → verifica con Firebase Admin SDK
     ✅ Válido → procesa con Gemini
     ❌ Inválido → intenta email+password del body
  2. ¿Tiene email+password? → verifica con Firebase Auth REST API
     ✅ Válido → procesa con Gemini
     ❌ Inválido → 401
  3. Nada → 401
```

> **FIREBASE_WEB_API_KEY** (en el cliente iOS) se resuelve automáticamente desde
> `app/google-services.json` (campo `current_key`) en build time.
> Se usa solo en login para intentar obtener un Bearer token.
> Si no está configurada, se omite el Bearer y solo se usa email+password.

---

## 🎨 UI — Atomic Design

```
atoms/         → CustomButton, CustomText, SafeImage, SafeIconPainter, Camera
molecules/     → LoginMolecules, ChatBotMolecules, MenuTopBar
organisms/     → LoginContent, ChatBotOrganisms, MenuOrganisms
               → CameraWithOverlaySection (expect/actual por plataforma)
templates/     → LoginTemplate, ChatBotTemplate, MenuTemplate
pages/         → LoginPage, ChatBotPage, MenuPage
               → NavigationMain() (expect/actual por plataforma)
```

### SafeImage / SafeIconPainter (expect/actual)

| Plataforma | SafeImage (PNG) | SafeIconPainter (XML icons) |
|---|---|---|
| Android | `painterResource(Res.drawable.xxx)` | `painterResource(Res.drawable.xxx)` |
| iOS | `preloadedBitmaps[path]` o fallback PNG | Material Icons (`rememberVectorPainter`) |
| Web | `painterResource(Res.drawable.xxx)` | `painterResource(Res.drawable.xxx)` |

---

## 🏗️ CI/CD

### Android CD (`cd.yml`)
- Trigger: push a `main`
- Firma con keystore desde GitHub Secrets
- Sube AAB a Play Store track `production`
- Secrets: `KEYSTORE_FILE`, `KEY_ALIAS`, `KEY_PASSWORD`, `STORE_PASSWORD`, `SERVICE_ACCOUNT_JSON`

### iOS build (`ios-build.yml`)
- Runner: `macos-latest`
- `chmod +x gradlew` siempre antes de Gradle
- Target: `linkDebugFrameworkIosSimulatorArm64`

### Web deploy
```bash
./gradlew :shared:wasmJsBrowserDistribution
firebase deploy --only hosting
```

---

## ⚠️ Decisiones de arquitectura y problemas conocidos

### 1. `navigation-compose` removido de commonMain
**Problema**: crash en iOS (`_NSCFNumber` / `Trace.uikit.kt`) con alpha 2.8.0.
**Solución**: State machine `var currentScreen` en cada plataforma (iOS, Web, Desktop).

### 2. NSURLSession no usable directamente en K/N 2.1.20
**Problema**: `dataTaskWithRequest:completionHandler:` no expuesto en stubs K/N 2.1.20.
**Solución**: Ktor Darwin (`ktor-client-darwin:3.1.0`) en iosMain.

### 3. Vectores XML drawable crash en iOS
**Problema**: `@android:color/xxx` en vectores XML no compatible con K/N.
**Solución**: `SafeIconPainter.ios.kt` mapea a Material Icons.

### 4. WasmJs no soporta `dynamic` ni lambdas como parámetros `external`
**Solución**: `@JsFun` con `JsAny` y polling con `delay()` para Promises.

### 5. Archivos corruptos (merge conflict no resuelto)
**Síntoma**: Imports en el medio del archivo, líneas mezcladas.
**Archivos afectados históricamente**: `MainiOS.kt`, `IosViewModelHolder.kt`, `IosAuthRepositoryImpl.kt`.
**Solución**: Reescribir el archivo completo desde cero con el orden correcto:
`package → imports → class/object`.

### 6. `gradle.properties` — `org.gradle.java.home`
No incluir rutas locales de Windows (`C:\Program Files\...`). GitHub Actions
configura `JAVA_HOME` automáticamente via `actions/setup-java`.

---

## 📋 Checklist — agregar nueva feature

### commonMain
- [ ] Definir interfaz/modelo en `domain/`
- [ ] Implementar UseCase en `domain/usecases/`
- [ ] Actualizar ViewModel en `presentation/viewmodels/`
- [ ] Agregar UiState si es necesario
- [ ] Registrar UseCase en `commonModule`

### Por plataforma
- [ ] Implementar repositorio/servicio en cada `*Main/`
- [ ] Registrar en el módulo Koin de la plataforma (`androidModule`, `iosModule`, `webModule`)
- [ ] Verificar `expect/actual` si se necesita comportamiento diferente por plataforma

### Compilar y verificar
```bash
# iOS
./gradlew :shared:compileKotlinIosSimulatorArm64

# Android
./gradlew :app:assembleDebug

# Web
./gradlew :shared:wasmJsBrowserDevelopmentRun
```

---

## 📌 Gestión de versión (`AppInfo`)

La versión se define **una sola vez** en `app/build.gradle.kts` y se propaga a todas las plataformas:

```
app/build.gradle.kts                shared/build.gradle.kts
  versionName = "3.3.0"   ──────►  extractAppVersion()
  versionCode = 27                      │
                                        ▼
                               SharedBuildConfig.kt (generado)
                                   APP_VERSION_NAME = "3.3.0"
                                   APP_VERSION_CODE = 27
                                        │
                                        ▼
                               AppInfo.versionName (default)
                               → usado en LoginContent: "v3.3.0"
```

| Plataforma | Cómo obtiene la versión | Notar |
|---|---|---|
| **Android** | `BuildConfig.VERSION_NAME` via `AppInfo.initialize()` en `MainActivity` | Coincide siempre con `build.gradle.kts` |
| **iOS** | `SharedBuildConfig.APP_VERSION_NAME` en compile time | **NO** llamar `AppInfo.initialize()` — el `Info.plist` de Xcode tiene "1.0" |
| **Web** | `SharedBuildConfig.APP_VERSION_NAME` en compile time | Sin inicialización adicional |

> ⚠️ **Regla importante**: En iOS NO se debe llamar `AppInfo.initialize()` con valores del
> `NSBundle` porque el `Info.plist` del proyecto Xcode tiene su propia versión independiente
> que sobreescribiría el valor correcto inyectado en compile time.
>
> Para cambiar la versión: solo editar `versionName` en `app/build.gradle.kts`.
> Se actualiza automáticamente en todas las plataformas en el siguiente build.



