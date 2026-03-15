const { GoogleGenerativeAI } = require("@google/generative-ai");
const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");

admin.initializeApp();

exports.askGemini = onRequest({ maxInstances: 10, memory: "512MiB", timeoutSeconds: 120 }, async (req, res) => {
  console.log("🔵 askGemini v3 - Con soporte multimodal de imagen");

  // Configurar CORS
  res.set('Access-Control-Allow-Origin', '*');
  res.set('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    return res.status(204).send('');
  }

  try {
    // Verificar el token de autenticación
    const authHeader = req.headers.authorization;
    if (!authHeader) {
      return res.status(401).json({ error: "No se proporcionó token de autenticación" });
    }

    const token = authHeader.split("Bearer ")[1];
    await admin.auth().verifyIdToken(token);
    console.log("✅ Token verificado correctamente");

    // Obtener la API key desde las variables de entorno
    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey) {
      console.error("❌ GEMINI_API_KEY no está configurada");
      return res.status(500).json({ error: "API Key no configurada" });
    }

    // Obtener el mensaje y opcionalmente la imagen del cuerpo de la solicitud
    const { message, conversationHistory = [], imageBase64, imageMimeType } = req.body;
    if (!message) {
      return res.status(400).json({ error: "No se proporcionó un mensaje" });
    }

    console.log(`📩 Mensaje recibido: "${message.substring(0, 80)}..."`);
    if (imageBase64) {
      console.log(`🖼️ Imagen recibida: longitud base64=${imageBase64.length}, ~${(imageBase64.length * 0.75 / 1024).toFixed(1)} KB, mimeType=${imageMimeType}`);
      console.log(`🖼️ Primeros 50 chars del base64: ${imageBase64.substring(0, 50)}...`);
    } else {
      console.log("📝 Sin imagen adjunta (imageBase64 es null/undefined)");
    }

    // Construir el prompt con el historial
    let prompt = "";
    if (imageBase64 && imageMimeType) {
      // Para análisis de imagen, usar el mensaje directamente como prompt
      // (el cliente ya envía el prompt adecuado: OCR o análisis visual)
      prompt = message;
    } else {
      // Para texto, construir prompt con historial
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

    let response;
    if (imageBase64 && imageMimeType) {
      // Llamada multimodal con imagen
      console.log("📷 Procesando imagen adjunta...");
      response = await callGeminiWithImage(apiKey, prompt, imageBase64, imageMimeType);
    } else {
      // Llamada solo texto
      response = await callGeminiAPI(apiKey, prompt);
    }

    return res.status(200).json({ response: response, success: true });

  } catch (error) {
    console.error("❌ Error en askGemini:", error);
    return res.status(500).json({ error: error.message, success: false });
  }
});

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
