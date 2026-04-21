package core.utils

object Constans {
    // URL de la Cloud Function askGemini (Firebase)
    const val GEMINI_FUNCTION_URL = "https://askgemini-766ctyoljq-uc.a.run.app"

    // URL directa de Gemini REST API para web (sin Cloud Function)
    const val GEMINI_DIRECT_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    // Firebase Auth REST API endpoint (usado por iOS para obtener Bearer token)
    const val FIREBASE_AUTH_URL =
        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword"

    // Firebase Web API Key — inyectado en build time desde local.properties
    // Ver: SharedBuildConfig (generado en build/generated/)
    // Nunca hardcodear aquí — usar local.properties igual que Android
    val FIREBASE_WEB_API_KEY: String get() = SharedBuildConfig.FIREBASE_WEB_API_KEY
    // ✅ NUEVO: endpoint para consultas BigQuery
    const val QUERY_FACTURAS_URL = "http://192.168.1.4:8000"
    // ← Ollama/FastAPI local (WiFi) — cambiar por Cloud Function al salir de la red local
    // Para producción: "https://queryollama-766ctyoljq-uc.a.run.app"
    const val OLLAMA_FACTURAS_URL  = "http://192.168.1.4:8000/query/"
}




