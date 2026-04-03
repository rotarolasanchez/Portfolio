const { GoogleGenerativeAI } = require("@google/generative-ai");
const { onRequest } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const admin = require("firebase-admin");
const https = require("https");

admin.initializeApp();

// Secretos de Cloud Functions
const geminiApiKey = defineSecret("GEMINI_API_KEY");
const webApiKey = defineSecret("WEB_API_KEY");

exports.askGemini = onRequest({
  maxInstances: 10,
  memory: "512MiB",
  timeoutSeconds: 120,
  secrets: [geminiApiKey, webApiKey]
}, async (req, res) => {
  console.log("🔵 askGemini v5 - Bearer con fallback email+password en mismo request");

  res.set('Access-Control-Allow-Origin', '*');
  res.set('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') return res.status(204).send('');

  try {
    // ── Autenticación v5: Bearer primero, email+password del body como fallback ──
    const authHeader = req.headers.authorization;
    const { email, password, message, conversationHistory = [], imageBase64, imageMimeType } = req.body;

    if (authHeader) {
      // Tiene Bearer token — intentar verificar (Android / Web / iOS con token válido)
      try {
        const token = authHeader.split("Bearer ")[1];
        await admin.auth().verifyIdToken(token);
        console.log("✅ Auth via Bearer token");
      } catch (bearerError) {
        // Bearer falló — intentar con email+password del body (iOS sin SDK Firebase)
        console.warn(`⚠️ Bearer inválido (${bearerError.code}), intentando email+password...`);
        if (email && password) {
          const key = (webApiKey.value() || "").trim();
          if (!key) return res.status(500).json({ error: "Configuración del servidor incompleta" });
          const authResult = await firebaseSignInRest(email, password, key);
          if (!authResult.idToken) return res.status(401).json({ error: "Email o contraseña incorrectos" });
          console.log(`✅ Auth via email+password fallback: ${email}`);
        } else {
          return res.status(401).json({ error: "No se proporcionó token de autenticación" });
        }
      }
    } else if (email && password) {
      // Sin Bearer — usar email+password directamente (iOS sin token)
      const key = (webApiKey.value() || "").trim();
      if (!key) return res.status(500).json({ error: "Configuración del servidor incompleta" });
      const authResult = await firebaseSignInRest(email, password, key);
      if (!authResult.idToken) return res.status(401).json({ error: "Email o contraseña incorrectos" });
      console.log(`✅ Auth via email+password (iOS): ${email}`);
    } else {
      return res.status(401).json({ error: "No se proporcionó token de autenticación" });
    }

    // ── Gemini API key ──
    const apiKey = geminiApiKey.value();
    if (!apiKey) return res.status(500).json({ error: "API Key no configurada" });
    if (!message) return res.status(400).json({ error: "No se proporcionó un mensaje" });

    console.log(`📩 Mensaje: "${message.substring(0, 80)}"`);

    // ── Construir prompt ──
    let prompt = "";
    if (imageBase64 && imageMimeType) {
      prompt = message;
    } else {
      if (conversationHistory.length > 0) {
        prompt = "Historial de conversación:\n";
        conversationHistory.forEach(msg => {
          prompt += `${msg.isUser ? "Usuario" : "Asistente"}: ${msg.text}\n`;
        });
        prompt += "\n";
      }
      prompt += `Nueva pregunta del usuario: ${message}\n`;
      prompt += "\nResponde de manera educativa y útil, manteniendo el contexto de la conversación anterior.";
    }

    console.log("🚀 Llamando a Gemini API...");
    const response = imageBase64 && imageMimeType
      ? await callGeminiWithImage(apiKey, prompt, imageBase64, imageMimeType)
      : await callGeminiAPI(apiKey, prompt);

    return res.status(200).json({ response, success: true });

  } catch (error) {
    console.error("❌ Error en askGemini:", error);
    return res.status(500).json({ error: error.message, success: false });
  }
});

// Autentica con Firebase Auth REST API — usado por iOS server-side
function firebaseSignInRest(email, password, webApiKey) {
  return new Promise((resolve, reject) => {
    const body = JSON.stringify({ email, password, returnSecureToken: true });
    const options = {
      hostname: "identitytoolkit.googleapis.com",
      path: `/v1/accounts:signInWithPassword?key=${webApiKey}`,
      method: "POST",
      headers: { "Content-Type": "application/json", "Content-Length": Buffer.byteLength(body) }
    };
    const req = https.request(options, (res) => {
      let data = "";
      res.on("data", chunk => data += chunk);
      res.on("end", () => {
        try { resolve(JSON.parse(data)); }
        catch (e) { reject(new Error("Respuesta inválida de Firebase Auth")); }
      });
    });
    req.on("error", reject);
    req.write(body);
    req.end();
  });
}



async function callGeminiAPI(apiKey, prompt) {
  const models = ["gemini-2.0-flash", "gemini-1.5-flash", "gemini-1.5-pro"];

  for (const modelName of models) {
    try {
      console.log(`🤖 Intentando con modelo: ${modelName}`);
      const genAI = new GoogleGenerativeAI(apiKey);
      const model = genAI.getGenerativeModel({ model: modelName });
      const result = await model.generateContent(prompt);
      const text = result.response.text();
      console.log(`✅ Respuesta recibida con modelo: ${modelName}`);
      return text;
    } catch (error) {
      console.warn(`⚠️ Falló modelo ${modelName}: ${error.message}`);
      if (modelName === models[models.length - 1]) {
        throw new Error(`Error al comunicarse con Gemini: ${error.message}`);
      }
    }
  }
}

async function callGeminiWithImage(apiKey, prompt, imageBase64, mimeType) {
  const models = ["gemini-2.0-flash", "gemini-1.5-flash", "gemini-1.5-pro"];

  for (const modelName of models) {
    try {
      console.log(`🤖 Intentando multimodal con modelo: ${modelName}`);
      const genAI = new GoogleGenerativeAI(apiKey);
      const model = genAI.getGenerativeModel({ model: modelName });

      const imagePart = {
        inlineData: {
          data: imageBase64,
          mimeType: mimeType
        }
      };

      const result = await model.generateContent([prompt, imagePart]);
      const text = result.response.text();
      console.log(`✅ Respuesta multimodal recibida con modelo: ${modelName}`);
      return text;
    } catch (error) {
      console.warn(`⚠️ Falló modelo multimodal ${modelName}: ${error.message}`);
      if (modelName === models[models.length - 1]) {
        throw new Error(`Error al comunicarse con Gemini: ${error.message}`);
      }
    }
  }
}
