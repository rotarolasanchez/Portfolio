const { GoogleGenerativeAI } = require("@google/generative-ai");
const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");

admin.initializeApp();

exports.askGemini = onRequest(async (req, res) => {
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

    console.log("✅ API Key detectada correctamente");

    // Obtener el mensaje del cuerpo de la solicitud
    const { message, conversationHistory = [] } = req.body;

    if (!message) {
      return res.status(400).json({ error: "No se proporcionó un mensaje" });
    }

    // Construir el prompt con el historial
    let prompt = "Historial de conversación:\n";
    conversationHistory.forEach(msg => {
      prompt += `${msg.isUser ? "Usuario" : "Asistente"}: ${msg.text}\n`;
    });
    prompt += `\nNueva pregunta del usuario: ${message}\n`;
    prompt += "\nResponde de manera educativa y útil, manteniendo el contexto de la conversación anterior.";

    console.log("🚀 Llamando a Gemini API con prompt:", prompt);

    // Llamar a Gemini API
    const response = await callGeminiAPI(apiKey, prompt);

    return res.status(200).json({ 
      response: response,
      success: true 
    });

  } catch (error) {
    console.error("❌ Error en askGemini:", error);
    return res.status(500).json({ 
      error: error.message,
      success: false 
    });
  }
});

async function callGeminiAPI(apiKey, prompt) {
  try {
    const genAI = new GoogleGenerativeAI(apiKey);
    const model = genAI.getGenerativeModel({ model: "gemini-2.0-flash-exp" });

    const result = await model.generateContent(prompt);
    const response = await result.response;
    const text = response.text();

    console.log("✅ Respuesta de Gemini recibida");
    return text;

  } catch (error) {
    console.error("❌ Error calling Gemini:", error);
    throw new Error(`Error al comunicarse con Gemini: ${error.message}`);
  }
}
