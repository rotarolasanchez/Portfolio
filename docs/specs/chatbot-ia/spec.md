# Spec: ChatBot con IA (modos Libre, Agente Comercial y Ollama + análisis de imágenes)

> Metodología: Spec-First | Estado: Completo | Fecha: 2026-11-20

---

## 1. Visión del Producto

Un chatbot multiplataforma que responde preguntas con Gemini, consulta facturas en BigQuery mediante lenguaje natural, o usa un modelo local Ollama, y además analiza imágenes capturadas con la cámara.

**Problema que resuelve:** Obtener respuestas de IA y consultar datos de facturación sin escribir SQL, desde cualquier plataforma.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| Usuario autenticado | Envía un mensaje en modo Libre | Respuesta de Gemini con el contexto de la conversación |
| Usuario autenticado | Envía una pregunta en modo Agente | Respuesta basada en consulta a BigQuery (facturas) |
| Usuario autenticado | Envía una pregunta en modo Ollama | Respuesta de IA local vía túnel |
| Usuario autenticado | Captura/adjunta una imagen | Análisis de la imagen mostrado en el chat |

**Usuario principal:** Usuario autenticado de la app

---

## 3. Funcionalidades

### En scope ✅
- Conversación con historial de mensajes (`ChatBotMessage`) y estados de carga/error.
- Tres modos de chat (`ChatMode`: LIBRE, AGENTE, OLLAMA) seleccionables; al cambiar se agrega un mensaje informativo del modo.
- Modo LIBRE: `ContinueChatUseCase` → Gemini vía Cloud Function `askGemini` (Bearer token con fallback email+password, soporte de imagen base64).
- Modo AGENTE: `QueryFacturasUseCase` → Cloud Function `queryFacturas` (Gemini genera consulta sobre BigQuery; requiere token Firebase).
- Modo OLLAMA: `QueryOllamaUseCase` → Cloud Function `queryOllama` (reenvía a servidor Ollama por túnel Cloudflare; requiere token Firebase).
- Análisis de imágenes (`AnalyzeImageUseCase`): OCR + Gemini en Android, Gemini directo en Web; captura mediante cámara por plataforma.
- Resolución de problemas (`SolveProblemUseCase`).
- Los errores se muestran como mensaje del bot (`Error: ...`).

### Fuera de scope ❌
- Persistencia del historial entre sesiones.
- Streaming de respuestas.
- Pantalla de chat completa con cámara en Desktop (implementación específica mínima).

---

## 4. Requisitos No Funcionales

- Claves (Gemini, Web API, URL de Ollama) almacenadas como secretos de Cloud Functions, nunca en el cliente.
- Cloud Functions con CORS, límites de memoria (512MiB) y timeouts (120–300 s).
- Endpoints de consulta protegidos con verificación de ID token de Firebase (401 sin token/ inválido).
- Tests unitarios: `ChatBotViewModelTest`, `ChatBotUiStateTest`, `ContinueChatUseCaseTest`, `SolveProblemUseCaseTest`, `ChatBotMessageTest`.

---

## 5. Criterios de Éxito

- [ ] Los mensajes enviados y respuestas se acumulan en el historial y `isLoading` vuelve a `false`.
- [ ] Cambiar de modo actualiza `chatMode` y agrega el mensaje informativo correspondiente.
- [ ] En modo LIBRE la respuesta proviene de Gemini; en AGENTE de BigQuery; en OLLAMA del servidor local.
- [ ] Una imagen adjunta produce el mensaje "Imagen adjuntada" y una respuesta de análisis, o un error si queda vacía.
- [ ] Las llamadas a `queryFacturas`/`queryOllama` sin token válido devuelven 401.
- [ ] Un error en cualquier modo se muestra en el chat sin bloquear la UI.

---

## 7. Archivos implementados

- shared/src/commonMain/kotlin/domain/model/ChatBot.kt
- shared/src/commonMain/kotlin/domain/repositories/ChatBotRepository.kt
- shared/src/commonMain/kotlin/domain/usecases/AnalyzeImageUseCase.kt
- shared/src/commonMain/kotlin/domain/usecases/ContinueChatUseCase.kt
- shared/src/commonMain/kotlin/domain/usecases/QueryOllamaUseCase.kt
- shared/src/commonMain/kotlin/domain/usecases/QuryFacturasUseCase.kt
- shared/src/commonMain/kotlin/domain/usecases/SolveProblemUseCase.kt
- shared/src/commonMain/kotlin/core/service/GeminiService.kt
- shared/src/commonMain/kotlin/core/service/GeminiCloudService.kt
- shared/src/commonMain/kotlin/core/service/TextRecongnitionService.kt
- shared/src/commonMain/kotlin/core/model/PlatformBitmap.kt
- shared/src/commonMain/kotlin/presentation/viewmodels/ChatBotViewModel.kt
- shared/src/commonMain/kotlin/presentation/state/ChatBotUiState.kt
- shared/src/commonMain/kotlin/presentation/view/pages/ChatBotPage.kt
- shared/src/commonMain/kotlin/presentation/view/templates/ChatBotTemplate.kt
- shared/src/commonMain/kotlin/presentation/view/organisms/ChatBotOrganisms.kt
- shared/src/commonMain/kotlin/presentation/view/molecules/ChatBotMoleculs.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/ChatBotAtoms.kt
- shared/src/commonMain/kotlin/presentation/view/atoms/Camera.kt
- shared/src/androidMain/kotlin/core/service/GeminiCloudService.kt
- shared/src/androidMain/kotlin/core/service/TextRecognitionServiceImpl.kt
- shared/src/androidMain/kotlin/data/repository/ChatRepositoryImpl.kt
- shared/src/androidMain/kotlin/di/ChatBotModule.kt
- shared/src/androidMain/kotlin/presentation/view/pages/ChatBotPageAndroid.kt
- shared/src/androidMain/kotlin/presentation/view/organisms/ChatBotOrganisms.android.kt
- shared/src/androidMain/kotlin/presentation/view/atoms/Camera.kt
- shared/src/iosMain/kotlin/data/repository/IosChatBotRepositoryImpl.kt
- shared/src/iosMain/kotlin/core/service/ServiceImplementations.kt
- shared/src/iosMain/kotlin/presentation/view/organisms/ChatBotOrganisms.ios.kt
- shared/src/iosMain/kotlin/presentation/view/atoms/Camera.kt
- shared/src/wasmJsMain/kotlin/di/WebChatBotRepositoryImpl.kt
- shared/src/wasmJsMain/kotlin/core/service/ServiceImplementations.kt
- shared/src/wasmJsMain/kotlin/core/utils/WebImageUtils.kt
- shared/src/wasmJsMain/kotlin/presentation/view/organisms/ChatBotOrganisms.wasm.kt
- shared/src/wasmJsMain/kotlin/presentation/view/atoms/Camera.kt
- shared/src/desktopMain/kotlin/presentation/view/organisms/ChatBotOrganisms.desktop.kt
- shared/src/desktopMain/kotlin/presentation/view/atoms/Camera.kt
- functions/index.js (askGemini, queryFacturas, queryOllama)
