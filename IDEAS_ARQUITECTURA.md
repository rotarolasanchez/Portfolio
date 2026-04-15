# 💡 Ideas de Arquitectura — Proyectos Futuros

> Documento generado el 2026-04-12 para preservar las ideas discutidas.
> Autor: rotarolasanchez

---

## 📋 Índice

1. [Idea 1 — Chatbot de Facturas con IA (SAP B1 → BigQuery → Gemini)](#idea-1)
2. [Idea 2 — Servicio de Score de Calidad de Imágenes con IA](#idea-2)
3. [Idea 3 — AI English Coach con Avatar animado ⭐ Mejor para portafolio](#idea-3)

---

<a id="idea-1"></a>
## 🧾 IDEA 1: Chatbot de Facturas con IA

### Descripción
Sistema que permite consultar datos de facturas empresariales en lenguaje natural, conectando SAP Business One (en Huawei Cloud) con BigQuery como capa analítica y Gemini como cerebro del chatbot.

### Problema que resuelve
- Los datos de facturas están en **SAP HANA B1** (Huawei Cloud Server)
- Consultarlos requiere conocimiento técnico de SAP
- Se necesita responder preguntas como: *"¿Cuánto vendimos en enero?"*, *"¿Cuál fue el producto más vendido este trimestre?"* sin necesidad de SQL o acceso directo a SAP

### Stack tecnológico
| Componente | Tecnología |
|---|---|
| ERP | SAP Business One + Service Layer REST API |
| Migración | Firebase Cloud Function (Node.js) |
| Base de datos analítica | Google BigQuery |
| IA / LLM | Google Gemini 2.5 Flash |
| Orquestación | Firebase Cloud Scheduler |
| Frontend chatbot | App KMP (Android/iOS/Web) |
| Autenticación | Firebase Auth |

---

### 🏗️ Arquitectura General

```
SAP Business One (Huawei Cloud)
        │
        │ Service Layer REST API
        │ https://server:50443/b1s/v1/
        ▼
Firebase Cloud Function: "syncFacturas"
  ├── Migración histórica (one-time)
  └── Sync incremental (Cloud Scheduler diario)
        │
        ▼
Google BigQuery
  ├── tabla: facturas
  ├── tabla: clientes
  ├── tabla: productos
  └── tabla: detalle_facturas
        │
        ▼
Firebase Cloud Function: "chatbotFacturas"
  ├── Fase 1: Gemini extrae parámetros del lenguaje natural
  ├── Fase 2: Genera SQL → ejecuta en BigQuery
  └── Fase 3: Gemini formatea respuesta para el usuario
        │
        ▼
App KMP (Android / iOS / Web)
  └── ChatBot UI (ya existente en el portafolio)
```

---

### 📦 Estructura SAP B1 Service Layer

**Autenticación (session-based):**
```
POST https://server:50443/b1s/v1/Login
Body: { "UserName": "...", "Password": "...", "CompanyDB": "..." }
Response: { "SessionId": "xxx" }  → usar como cookie B1SESSION
```

**Endpoints útiles:**
```
GET /Invoices?$filter=DocDate ge '2025-01-01'
GET /BusinessPartners
GET /Items
GET /SalesOrders
```

> ⚠️ El SessionId expira en 30 minutos. Implementar caché de sesión en la Cloud Function.

---

### 🗄️ Schema BigQuery

```sql
-- Tabla principal de facturas
CREATE TABLE `proyecto.dataset.facturas` (
  doc_num        INT64,
  doc_date       DATE,
  doc_due_date   DATE,
  card_code      STRING,      -- Código cliente
  card_name      STRING,      -- Nombre cliente
  doc_total      FLOAT64,
  doc_currency   STRING,
  comments       STRING,
  doc_status     STRING,      -- O=Abierta, C=Cerrada
  created_at     TIMESTAMP
);

-- Tabla de líneas de detalle
CREATE TABLE `proyecto.dataset.detalle_facturas` (
  doc_num        INT64,
  line_num       INT64,
  item_code      STRING,
  item_desc      STRING,
  quantity       FLOAT64,
  unit_price     FLOAT64,
  line_total     FLOAT64,
  currency       STRING
);
```

---

### ⚡ Cloud Functions a implementar

#### `syncFacturas` — Sincronización incremental (diaria)
```javascript
// Trigger: Cloud Scheduler (cada día a las 2:00 AM)
exports.syncFacturas = onRequest({ secrets: [sapCredentials] }, async (req, res) => {
  // 1. Login a SAP B1 Service Layer
  // 2. Obtener facturas desde última fecha de sync
  // 3. Insertar/actualizar en BigQuery (MERGE)
  // 4. Guardar timestamp de última sync en Firestore
});
```

#### `chatbotFacturas` — Consultas en lenguaje natural
```javascript
// Flujo de 3 fases (RAG con Text-to-SQL)
exports.chatbotFacturas = onRequest({ secrets: [geminiApiKey] }, async (req, res) => {
  const { message } = req.body;

  // Fase 1: Gemini extrae intención y parámetros
  const params = await gemini.extract(`
    Del mensaje: "${message}"
    Extrae: fecha_inicio, fecha_fin, cliente, producto, tipo_consulta
    Responde en JSON.
  `);

  // Fase 2: Generar y ejecutar SQL en BigQuery
  const sql = await gemini.generateSQL(params, BIGQUERY_SCHEMA);
  const data = await bigquery.query(sql);

  // Fase 3: Gemini formatea la respuesta
  const response = await gemini.format(`
    El usuario preguntó: "${message}"
    Los datos son: ${JSON.stringify(data)}
    Responde de forma natural y amigable.
  `);

  return res.json({ response });
});
```

---

### 📅 Plan de implementación

| Fase | Descripción | Estimado |
|---|---|---|
| 1 | Crear dataset en BigQuery + schema | 1 día |
| 2 | Cloud Function `syncFacturas` (migración histórica) | 2-3 días |
| 3 | Configurar Cloud Scheduler para sync diaria | 1 día |
| 4 | Cloud Function `chatbotFacturas` (RAG + Text-to-SQL) | 3-4 días |
| 5 | Integrar chatbot UI en app KMP | 2 días |
| **Total** | | **~10 días** |

---

### 💰 Costos estimados (mensual)

| Servicio | Costo aprox. |
|---|---|
| BigQuery (1-10 GB data, 100 consultas/mes) | **~$0 - $5** (free tier generoso) |
| Gemini API (1,000 llamadas/mes) | **~$0** (free tier) |
| Cloud Functions (1,000 invocaciones/mes) | **~$0** (free tier) |
| Cloud Scheduler (1 job) | **~$0** (3 jobs gratis) |
| **Total** | **~$0-5/mes** |

---

<a id="idea-2"></a>
## 🖼️ IDEA 2: Servicio Web de Score de Calidad de Imágenes

### Descripción
API REST que recibe una imagen y retorna:
- **Endpoint 1** `/evaluarCalidadImagen`: Score de calidad fotográfica (0-100)
- **Endpoint 2** `/reconocerProducto`: Identificación de objeto/producto/marca

Diseñado para ser consumido por:
- **Web interna** (via `X-API-Key`)
- **App móvil KMP** (via Firebase Bearer Token)

### Problema que resuelve
- Verificar calidad de fotos antes de subirlas a un catálogo
- Identificar automáticamente productos en imágenes
- Evitar imágenes borrosas, mal iluminadas o mal encuadradas

---

### 🏗️ Arquitectura

```
Cliente Web (X-API-Key header)  ──┐
                                   ├──► Firebase Cloud Function
App Móvil KMP (Bearer Token)  ────┘         │
                                             │
                               ┌─────────────┴─────────────┐
                               │                           │
                    evaluarCalidadImagen         reconocerProducto
                               │                           │
                               └─────────────┬─────────────┘
                                             │
                                    Gemini 2.5 Flash
                                    (Vision multimodal)
```

---

### 🔐 Autenticación Flexible

```
Request Headers:
  ├── X-API-Key: <clave-externa>     → para clientes web
  └── Authorization: Bearer <token>  → para app móvil (Firebase Auth)
```

**Secretos Firebase:**
```
GEMINI_API_KEY    → API Key de Gemini (ya existente)
EXTERNAL_API_KEY  → API Key para clientes web (nueva)
```

---

### 📡 Endpoint 1: `evaluarCalidadImagen`

**Request:**
```
POST https://us-central1-portfolio-app-9a4bc.cloudfunctions.net/evaluarCalidadImagen
Headers:
  Content-Type: application/json
  X-API-Key: <tu-api-key>           ← web
  Authorization: Bearer <token>      ← móvil

Body:
{
  "imageBase64": "<imagen en base64>",
  "imageMimeType": "image/jpeg"
}
```

**Response:**
```json
{
  "puntuacion_total": 78,
  "categorias": {
    "iluminacion": 85,
    "enfoque": 70,
    "composicion": 80,
    "exposicion": 75
  },
  "veredicto": "buena",
  "observaciones": "Buena iluminación natural. Ligero desenfoque en los bordes.",
  "success": true
}
```

**Veredictos posibles:**
| Rango | Veredicto |
|---|---|
| 90-100 | `excelente` |
| 70-89 | `buena` |
| 50-69 | `aceptable` |
| 0-49 | `mala` |

---

### 📡 Endpoint 2: `reconocerProducto`

**Request:**
```
POST https://us-central1-portfolio-app-9a4bc.cloudfunctions.net/reconocerProducto
Headers:
  Content-Type: application/json
  X-API-Key: <tu-api-key>

Body:
{
  "imageBase64": "<imagen en base64>",
  "imageMimeType": "image/jpeg"
}
```

**Response:**
```json
{
  "objeto": "Laptop",
  "marca": "Dell",
  "categoria": "Electrónicos",
  "confianza": 92,
  "descripcion": "Laptop Dell color plateado, posiblemente XPS o Inspiron",
  "atributos": ["plateado", "15 pulgadas aprox.", "teclado retroiluminado"],
  "success": true
}
```

---

### 💻 Ejemplo de consumo desde JavaScript (Web)

```javascript
async function evaluarImagen(file) {
  // 1. Comprimir imagen (recomendado: max 1MB)
  const base64 = await fileToBase64(file);

  // 2. Llamar al servicio
  const response = await fetch(
    'https://us-central1-portfolio-app-9a4bc.cloudfunctions.net/evaluarCalidadImagen',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-API-Key': 'tu-api-key-aqui'
      },
      body: JSON.stringify({
        imageBase64: base64,
        imageMimeType: file.type
      })
    }
  );

  const result = await response.json();
  console.log(`Score: ${result.puntuacion_total}/100 — ${result.veredicto}`);
  return result;
}

function fileToBase64(file) {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (e) => resolve(e.target.result.split(',')[1]);
    reader.readAsDataURL(file);
  });
}
```

---

### 📱 Ejemplo de consumo desde Kotlin (App KMP)

```kotlin
// En ChatBotViewModel o nuevo ImageScoringViewModel
suspend fun evaluarCalidadImagen(imageBytes: ByteArray): ImageScoreResult {
    val base64 = Base64.encode(imageBytes)
    val token = authRepository.getIdToken()

    return httpClient.post("$FUNCTION_URL/evaluarCalidadImagen") {
        header("Authorization", "Bearer $token")
        contentType(ContentType.Application.Json)
        setBody(mapOf(
            "imageBase64" to base64,
            "imageMimeType" to "image/jpeg"
        ))
    }.body<ImageScoreResult>()
}

data class ImageScoreResult(
    val puntuacion_total: Int,
    val veredicto: String,          // "excelente" | "buena" | "aceptable" | "mala"
    val observaciones: String,
    val categorias: Map<String, Int>
)
```

---

### 📅 Plan de implementación

| Fase | Descripción | Estimado |
|---|---|---|
| 1 | Agregar secreto `EXTERNAL_API_KEY` en Firebase | 10 min |
| 2 | Implementar `authenticateFlexible()` en index.js | 30 min |
| 3 | Implementar endpoint `evaluarCalidadImagen` | 1 hora |
| 4 | Implementar endpoint `reconocerProducto` | 1 hora |
| 5 | Deploy a Firebase Functions | 10 min |
| 6 | Probar con Postman/curl | 30 min |
| 7 | (Opcional) Integrar en app KMP | 2-3 días |
| **Total** | | **~1-2 días** |

---

### 💰 Costos estimados (mensual)

| Servicio | Costo aprox. |
|---|---|
| Cloud Functions (1K invocaciones) | **~$0** |
| Gemini 2.5 Flash Vision | **~$0.075 por 1K imágenes** |
| **Total para 1,000 imágenes/mes** | **~$0.075** |

---

---

<a id="idea-3"></a>
## 🤖 IDEA 3: AI English Coach — Chatbot con Avatar para aprender inglés

### Descripción
Aplicación de aprendizaje de inglés con un asistente de IA conversacional que incluye:
- **Avatar animado** que "habla" y reacciona
- **Reconocimiento de voz** (el usuario habla en inglés)
- **Corrección de gramática** y vocabulario en tiempo real
- **Conversación guiada** por nivel (A1 → C2)
- **Feedback de pronunciación** basado en la transcripción

> 🏆 **Alto impacto en portafolio**: Combina IA, voz, animación y KMP en un solo proyecto.

---

### 🎯 ¿Por qué es ideal para un portafolio?

| Skill que demuestra | Tecnología |
|---|---|
| IA / LLM | Gemini para conversación + corrección |
| Multiplatform | KMP: Android, iOS, Web (un solo código) |
| Voz / Audio | STT y TTS por plataforma |
| Animación | Lottie (avatar animado cross-platform) |
| Arquitectura | Clean Architecture + MVVM |
| Backend | Firebase Cloud Functions |
| UX moderna | Animaciones, feedback visual, progreso |

---

### 🧩 Opciones de Avatar (comparativa)

| Opción | Costo | Complejidad | Impacto visual | ¿KMP compatible? |
|---|---|---|---|---|
| **Lottie Animation** ⭐ | Gratis | Baja | ★★★★☆ | ✅ Sí (todas las plataformas) |
| Ready Player Me (iframe) | Gratis | Media | ★★★★★ | ⚠️ Solo Web |
| D-ID / HeyGen (video AI) | $$ | Alta | ★★★★★ | ⚠️ Solo Web/video |
| SVG animado custom | Gratis | Media | ★★★☆☆ | ✅ Sí |
| Three.js / 3D model | Gratis | Muy alta | ★★★★★ | ❌ No |

### ✅ Recomendación: **Lottie Animation**
- Compatible con todas las plataformas KMP
- Librería: `io.github.alexzhirkevich:compottie` (Compose Multiplatform)
- Miles de avatares gratuitos en [LottieFiles.com](https://lottiefiles.com)
- El avatar puede tener estados: `idle`, `talking`, `listening`, `thinking`

---

### 🏗️ Arquitectura

```
Usuario habla (micrófono)
        │
        ▼
Speech-to-Text (por plataforma)
  ├── Android: SpeechRecognizer API
  ├── iOS: AVSpeechRecognizer
  └── Web: Web Speech API (wasmJs)
        │
        ▼
Firebase Cloud Function: "englishCoach"
  ├── Gemini: corrige gramática + responde
  └── Retorna: { response, corrections, score }
        │
        ▼
Text-to-Speech (por plataforma)
  ├── Android: TextToSpeech API
  ├── iOS: AVSpeechSynthesizer
  └── Web: SpeechSynthesis API
        │
        ▼
Avatar Lottie — cambia estado según fase
  ├── 🟡 "thinking" → mientras espera Gemini
  ├── 🔵 "talking"  → mientras reproduce TTS
  └── 🟢 "listening" → mientras escucha al usuario
```

---

### 🎙️ Speech-to-Text por plataforma (expect/actual)

```kotlin
// commonMain — interfaz
expect class SpeechRecognizer {
    fun startListening(onResult: (String) -> Unit, onError: () -> Unit)
    fun stopListening()
}

// androidMain
actual class SpeechRecognizer {
    actual fun startListening(onResult: (String) -> Unit, onError: () -> Unit) {
        // android.speech.SpeechRecognizer
    }
}

// iosMain
actual class SpeechRecognizer {
    actual fun startListening(onResult: (String) -> Unit, onError: () -> Unit) {
        // AVSpeechRecognizer + SFSpeechRecognizer
    }
}

// wasmJsMain
actual class SpeechRecognizer {
    actual fun startListening(onResult: (String) -> Unit, onError: () -> Unit) {
        // window.SpeechRecognition (Web Speech API)
    }
}
```

---

### ☁️ Cloud Function: `englishCoach`

```javascript
exports.englishCoach = onRequest({
  secrets: [geminiApiKey, externalApiKey],
  memory: "512MiB",
  timeoutSeconds: 60
}, async (req, res) => {

  await authenticateFlexible(req, externalApiKey.value());

  const { userMessage, level = "B1", conversationHistory = [] } = req.body;

  const systemPrompt = `
Eres un profesor de inglés amigable y paciente llamado "Alex".
El estudiante está en nivel ${level} (CEFR).

INSTRUCCIONES:
1. Responde SIEMPRE en inglés, pero incluye la corrección en español
2. Si el estudiante cometió errores gramaticales, corrígelos amablemente
3. Mantén la conversación natural y educativa
4. Sugiere vocabulario alternativo cuando sea apropiado

Responde en este JSON exacto:
{
  "response": "<tu respuesta en inglés>",
  "corrections": [
    { "original": "texto incorrecto", "corrected": "texto correcto", "explanation": "explicación en español" }
  ],
  "vocabulary": [{ "word": "palabra", "meaning": "significado", "example": "ejemplo" }],
  "score": <número 0-100 de la calidad del mensaje del usuario>
}`;

  const prompt = [
    systemPrompt,
    ...conversationHistory.map(m => `${m.role}: ${m.text}`),
    `Student: ${userMessage}`
  ].join('\n');

  const raw = await callGeminiAPI(geminiApiKey.value(), prompt);
  const json = JSON.parse(raw.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim());

  return res.json({ ...json, success: true });
});
```

---

### 📱 ViewModel en Kotlin (commonMain)

```kotlin
data class EnglishCoachUiState(
    val messages: List<CoachMessage> = emptyList(),
    val avatarState: AvatarState = AvatarState.IDLE,
    val isListening: Boolean = false,
    val isLoading: Boolean = false,
    val userLevel: String = "B1"
)

enum class AvatarState { IDLE, LISTENING, THINKING, TALKING }

data class CoachMessage(
    val text: String,
    val isUser: Boolean,
    val corrections: List<Correction> = emptyList(),
    val score: Int? = null
)

class EnglishCoachViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EnglishCoachUiState())
    val uiState: StateFlow<EnglishCoachUiState> = _uiState

    fun startListening() {
        _uiState.update { it.copy(avatarState = AvatarState.LISTENING, isListening = true) }
        speechRecognizer.startListening(
            onResult = { text -> sendMessage(text) },
            onError = { stopListening() }
        )
    }

    fun sendMessage(userText: String) {
        _uiState.update { it.copy(avatarState = AvatarState.THINKING, isLoading = true) }
        viewModelScope.launch {
            val result = coachRepository.askCoach(userText, _uiState.value.userLevel)
            _uiState.update { state ->
                state.copy(
                    messages = state.messages + listOf(
                        CoachMessage(userText, isUser = true, score = result.score),
                        CoachMessage(result.response, isUser = false, corrections = result.corrections)
                    ),
                    avatarState = AvatarState.TALKING,
                    isLoading = false
                )
            }
            textToSpeech.speak(result.response) {
                _uiState.update { it.copy(avatarState = AvatarState.IDLE) }
            }
        }
    }
}
```

---

### 🎨 UI con Lottie Avatar (Compose Multiplatform)

```kotlin
// build.gradle.kts — agregar dependencia
implementation("io.github.alexzhirkevich:compottie:2.0.0")

// Composable del avatar
@Composable
fun EnglishCoachAvatar(state: AvatarState) {
    val lottieFile = when (state) {
        AvatarState.IDLE      -> Res.files.avatar_idle
        AvatarState.LISTENING -> Res.files.avatar_listening
        AvatarState.THINKING  -> Res.files.avatar_thinking
        AvatarState.TALKING   -> Res.files.avatar_talking
    }

    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes(lottieFile).decodeToString()
        )
    }

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.size(200.dp)
    )
}
```

---

### ✨ Features del MVP

| Feature | Descripción | Dificultad |
|---|---|---|
| 💬 Chat de texto | Conversar por escrito con el coach | Fácil |
| 🎙️ Chat de voz | Hablar y recibir respuesta hablada | Media |
| ✏️ Corrección inline | Mostrar errores en el mensaje del usuario | Fácil |
| 🏆 Score por mensaje | Puntuación de 0-100 de la calidad | Fácil |
| 📊 Progreso | Gráfica de mejora a lo largo del tiempo | Media |
| 🎭 Avatar animado | Lottie con estados idle/listening/talking | Media |
| 📚 Word of the day | Vocabulario nuevo cada día (Firestore) | Fácil |
| 🎯 Niveles CEFR | A1, A2, B1, B2, C1, C2 | Fácil |

---

### 📅 Plan de implementación (MVP en 2 semanas)

| Día | Tarea |
|---|---|
| 1 | Cloud Function `englishCoach` + desplegar |
| 2 | `EnglishCoachViewModel` + modelo de datos |
| 3 | UI básica: chat de texto + respuestas |
| 4 | Integrar Lottie avatar (4 estados) |
| 5 | STT en Android (SpeechRecognizer) |
| 6 | TTS en Android (TextToSpeech) |
| 7 | STT/TTS en Web (Web Speech API) |
| 8 | Correcciones inline en el chat |
| 9 | Score + historial en Firestore |
| 10 | Pulir UI, animaciones, UX |

---

### 💰 Costos estimados

| Servicio | Costo |
|---|---|
| Gemini 2.5 Flash (1K conversaciones/mes) | **~$0** (free tier) |
| Firebase Cloud Functions | **~$0** (free tier) |
| Firestore (historial) | **~$0** (free tier) |
| Lottie animations | **Gratis** (LottieFiles.com) |
| **Total** | **$0/mes** |

---

### 🏆 Valor de portafolio vs esfuerzo

```
Alta complejidad percibida  ████████████████░░░░  80%
Tiempo real de desarrollo   ████████░░░░░░░░░░░░  ~2 semanas
Diferenciación              █████████████████░░░  85%
Tecnologías en tendencia    ████████████████████  100%

Tecnologías que muestra:
  ✅ Kotlin Multiplatform (Android + iOS + Web)
  ✅ AI/LLM (Gemini)
  ✅ Voice/Speech (STT + TTS)
  ✅ Animations (Lottie Compose)
  ✅ Clean Architecture + MVVM
  ✅ Firebase (Auth + Functions + Firestore)
  ✅ EdTech (nicho interesante para recruiters)
```

---

### 📁 Archivos a crear

```
shared/src/commonMain/kotlin/
  ├── domain/model/CoachMessage.kt
  ├── domain/model/EnglishLevel.kt
  ├── domain/repositories/EnglishCoachRepository.kt
  ├── domain/usecases/SendMessageToCoachUseCase.kt
  ├── presentation/state/EnglishCoachUiState.kt
  ├── presentation/viewmodels/EnglishCoachViewModel.kt
  └── presentation/view/pages/EnglishCoachPage.kt

shared/src/androidMain/kotlin/
  └── core/speech/SpeechRecognizer.android.kt

shared/src/iosMain/kotlin/
  └── core/speech/SpeechRecognizer.ios.kt

shared/src/wasmJsMain/kotlin/
  └── core/speech/SpeechRecognizer.wasmJs.kt

shared/src/commonMain/composeResources/files/
  ├── avatar_idle.json        ← descargar de LottieFiles
  ├── avatar_listening.json
  ├── avatar_thinking.json
  └── avatar_talking.json

functions/
  └── index.js  ← agregar englishCoach endpoint
```

---

## 🗺️ Roadmap sugerido

```
Semana 1-2:  IDEA 3 — English Coach con Avatar (más impactante para portafolio)
                └── Cloud Function englishCoach
                └── Chat de texto + correcciones
                └── Avatar Lottie animado
                └── Voz (STT + TTS) por plataforma

Semana 3-4:  IDEA 2 — Servicio de imágenes (más rápido, ya tienes la base)
                └── Implementar endpoints en index.js
                └── Configurar EXTERNAL_API_KEY
                └── Deploy + pruebas

Semana 5-8:  IDEA 1 — Chatbot de facturas (más complejo, uso empresarial)
                └── Configurar BigQuery dataset
                └── Cloud Function de migración SAP → BigQuery
                └── Configurar Cloud Scheduler
                └── Implementar chatbotFacturas (RAG + Text-to-SQL)
                └── Integrar en chatbot KMP existente
```

---

## 📁 Archivos a crear/modificar

### IDEA 2 (Imágenes):
- `functions/index.js` → agregar 2 endpoints + auth flexible

### IDEA 1 (Facturas SAP):
- `functions/index.js` → agregar `syncFacturas` y `chatbotFacturas`
- `functions/bigquery-client.js` → helper de BigQuery (nuevo)
- `functions/sap-service-layer.js` → helper de SAP B1 (nuevo)
- `shared/src/commonMain/.../model/InvoiceModel.kt` → modelo de facturas
- `shared/src/commonMain/.../viewmodels/InvoiceChatViewModel.kt` → ViewModel

---

> 📌 **Nota**: Este documento es una referencia de diseño.
> El código exacto listo para implementar fue generado en la sesión anterior.
> Para la IDEA 2, el código completo está en la conversación de GitHub Copilot del 2026-04-12.



