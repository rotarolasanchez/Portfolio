package core.utils

object Constans {
    // Endpoint compartido: Android usa Bearer token, iOS usa email+password en body
    const val GEMINI_FUNCTION_URL = "https://us-central1-portfolio-app-9a4bc.cloudfunctions.net/askGemini"
    const val GEMINI_DIRECT_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
}

