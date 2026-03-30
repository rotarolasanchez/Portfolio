# GitHub Copilot Instructions — Portafolio KMP

## Descripción del proyecto
Aplicación **Kotlin Multiplatform (KMP)** con **Compose Multiplatform** que comparte código entre:
- **Android** (principal, producción en Play Store)
- **Web** (WasmJs, desplegado en Firebase Hosting)
- **iOS** (framework compartido, compilación en GitHub Actions con runner macOS)
- **Desktop** (JVM, experimental)

## Stack tecnológico

### Core
- **Kotlin Multiplatform** con módulo `shared` como fuente de verdad
- **Compose Multiplatform** para UI en todas las plataformas
- **Koin** para inyección de dependencias (NO usar Hilt/Dagger, solo Koin)
- **Kotlin Coroutines** para código asíncrono
- **Kotlin Navigation Compose** `2.8.0-alpha10` para navegación

### Por plataforma
| Plataforma | DI | HTTP | Auth | OCR |
|---|---|---|---|---|
| Android | `koin-android` + `koin-androidx-compose` | OkHttp | Firebase Auth | ML Kit |
| iOS | `koin-core` | NSURLSession (Kotlin/Native) | Stub | Stub (Vision pendiente) |
| Web (WasmJs) | `koin-core` | Fetch API JS | Firebase JS SDK | No aplica |
| Desktop | `koin-core` | — | — | — |

### Backend
- **Firebase Cloud Functions** (Node.js) — endpoint `askGemini`
- **Gemini API** vía Cloud Function en `https://askgemini-766ctyoljq-uc.a.run.app`
- **Firebase Auth** para autenticación
- **Firebase Firestore** para persistencia

## Estructura del proyecto

```
portafolio_kotlin/
├── app/                        # Módulo Android principal
├── shared/                     # Módulo KMP compartido
│   └── src/
│       ├── commonMain/kotlin/
│       │   ├── core/
│       │   │   ├── service/    # Interfaces: GeminiCloudService, TextRecognitionService
│       │   │   └── utils/      # Constans (URLs, config)
│       │   ├── data/
│       │   │   ├── datasources/ # AuthDataSourceI (interfaz)
│       │   │   └── repository/  # AuthRepositoryImpl, ChatRepositoryImpl
│       │   ├── di/              # commonModule Koin
│       │   ├── domain/
│       │   │   ├── model/       # UserModel, ChatBotMessage, MenuModel
│       │   │   ├── repositories/ # AuthRepository, ChatRepository (interfaces)
│       │   │   └── usecases/    # LoginUseCase, LogoutUseCase, AnalyzeImageUseCase...
│       │   └── presentation/
│       │       ├── state/       # AuthUiState, ChatBotUiState, CameraUiState
│       │       ├── view/
│       │       │   ├── atoms/   # Componentes mínimos reutilizables
│       │       │   ├── molecules/ # Composables de nivel medio
│       │       │   ├── organisms/ # LoginContent, ChatBotOrganisms, MenuOrganisms
│       │       │   ├── templates/ # LoginTemplate, ChatBotTemplate
│       │       │   └── pages/   # AppPage (navegación), LoginPage, ChatBotPage, MenuPage
│       │       └── viewmodels/  # AuthViewModel, ChatBotViewModel, MenuViewModel
│       ├── androidMain/kotlin/
│       │   ├── core/service/    # ServiceImplementations (OkHttp + ML Kit)
│       │   ├── data/            # AuthDataSourceImpl (Firebase), ChatRepositoryImpl
│       │   ├── di/              # androidModule Koin (AuthModule, ChatBotModule)
│       │   └── presentation/    # actual fun provideXxxViewModel() con koinViewModel()
│       ├── iosMain/kotlin/
│       │   ├── core/service/    # ServiceImplementations (NSURLSession)
│       │   └── presentation/    # actual fun provideXxxViewModel() con GlobalContext.get().get<T>()
│       └── wasmJsMain/kotlin/
│           ├── core/service/    # ServiceImplementations (Fetch API)
│           ├── di/              # webModule Koin
│           └── presentation/    # actual fun provideXxxViewModel()
├── functions/                   # Firebase Cloud Functions (Node.js)
│   └── index.js                 # askGemini endpoint
├── .github/
│   └── workflows/
│       ├── cd.yml               # CD: build AAB + upload Play Store
│       ├── ci.yml               # CI: tests + lint
│       └── ios-build.yml        # iOS framework build (runner macOS)
└── iosApp/                      # Proyecto Xcode que consume el framework KMP
```

## Convenciones de código

### expect/actual pattern (KMP)
```kotlin
// commonMain — declaración
expect fun provideXxxViewModel(): XxxViewModel

// androidMain — implementación
actual fun provideXxxViewModel(): XxxViewModel = koinViewModel()

// iosMain — implementación
actual fun provideXxxViewModel(): XxxViewModel = GlobalContext.get().get<XxxViewModel>()

// wasmJsMain — implementación
actual fun provideXxxViewModel(): XxxViewModel = KoinPlatform.getKoin().get()
```

### Inyección de dependencias con Koin
- **NUNCA** usar `@Inject`, `@HiltViewModel`, `@InstallIn` — son de Hilt, no compatible con KMP
- En `commonMain`: usar `by inject()` o pasar por parámetro
- En `androidMain`: usar `koinViewModel()` en Composables
- En `iosMain`: usar `GlobalContext.get().get<T>()`
- En `wasmJsMain`: usar `KoinPlatform.getKoin().get()`

### Plataforma-específico en Compose
- Código de **cámara** (CameraX) → solo `androidMain`
- Código de **NSURLSession** → solo `iosMain`
- Código de **Fetch API** → solo `wasmJsMain`
- **No usar** `LocalContext.current`, `AndroidView`, `@AndroidEntryPoint` en `commonMain`

### Recursos (Compose Multiplatform Resources)
- Imágenes en `shared/src/commonMain/composeResources/drawable/`
- Acceso: `painterResource(Res.drawable.nombre_imagen)`
- **No usar** `R.drawable.xxx` en commonMain (solo en androidMain)
- Los vectores drawable NO pueden usar `@android:color/xxx` — usar valores hex directos

### Navegación
```kotlin
// En AppPage.kt (commonMain)
NavHost(navController, startDestination = "login") {
    composable("login") { LoginPage() }
    composable("chatbot") { ChatBotPage() }
    composable("menu") { MenuPage() }
}
```

## Reglas importantes

1. **Koin en lugar de Hilt** para toda la inyección de dependencias
2. **No duplicar** funciones `expect`/`actual` — cada una debe estar exactamente una vez en commonMain y una vez en cada plataforma
3. **NSURLSession en iOS** — usar trailing lambda, no parámetro nombrado `completionHandler`
4. **Tipos genéricos explícitos en iOS** — `GlobalContext.get().get<T>()` no `get()`
5. **Try-catch no permitido** alrededor de llamadas a Composable functions
6. **WasmJs** no soporta el tipo `dynamic` de JS directamente
7. **Versión de la app** se gestiona en `app/build.gradle.kts` con `versionCode` y `versionName`

## CI/CD

### Android CD (`cd.yml`)
- Trigger: push a `main`
- Firma con keystore desde GitHub Secrets
- Sube AAB a Play Store track `production` con `changesNotSentForReview: true`
- Requiere `KEYSTORE_FILE`, `KEY_ALIAS`, `KEY_PASSWORD`, `STORE_PASSWORD`, `SERVICE_ACCOUNT_JSON`

### iOS build (`ios-build.yml`)
- Runner: `macos-latest`
- Siempre ejecutar `chmod +x gradlew` antes de invocar Gradle
- Target: `linkDebugFrameworkIosSimulatorArm64`

### Web deploy
- Build con `./gradlew :shared:wasmJsBrowserDistribution`
- Deploy con `firebase deploy --only hosting`

## Notas de arquitectura
- La arquitectura sigue **Clean Architecture** con capas: `data → domain → presentation`
- Los ViewModels heredan de `ViewModel` (androidx) — compatible con KMP vía `lifecycle-viewmodel`
- El patrón UI es **Atomic Design**: atoms → molecules → organisms → templates → pages
- Firebase solo se usa en Android y Web (no hay SDK oficial de Firebase para iOS en KMP)

