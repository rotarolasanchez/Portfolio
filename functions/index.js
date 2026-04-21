const { GoogleGenerativeAI } = require("@google/generative-ai");
const { onRequest } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const admin = require("firebase-admin");
const https = require("https");

admin.initializeApp();

// Secretos de Cloud Functions
const geminiApiKey = defineSecret("GEMINI_API_KEY");
const webApiKey = defineSecret("WEB_API_KEY");

// En functions/index.js — agregar esta función
const { BigQuery } = require('@google-cloud/bigquery');
const bigquery = new BigQuery();

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
  const models = [
    "gemini-2.5-flash",
    "gemini-2.0-flash-lite",
    "gemini-1.5-flash-latest",
    "gemini-1.5-flash"
  ];

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
  const models = [
    "gemini-2.5-flash",
    "gemini-2.0-flash-lite",
    "gemini-1.5-flash-latest",
    "gemini-1.5-flash"
  ];

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



exports.uploadExcelToBigQuery = onRequest({
  cors: true,
  memory: "512MiB",
  timeoutSeconds: 120,
}, async (req, res) => {
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  try {
    const token = req.headers.authorization?.replace('Bearer ', '');
    if (!token) return res.status(401).json({ error: 'No token' });
    await admin.auth().verifyIdToken(token);
  } catch {
    return res.status(401).json({ error: 'Token inválido' });
  }

  const { facturas } = req.body;
  if (!Array.isArray(facturas) || facturas.length === 0) {
    return res.status(400).json({ error: 'No hay datos para insertar' });
  }

  // Debug: ver qué claves llegan realmente del frontend
  console.log('🔍 Primera fila recibida:', JSON.stringify(facturas[0]));
  console.log('🔍 Claves disponibles:', Object.keys(facturas[0]));

  const parseNum = (val) => {
    if (val === undefined || val === null) return 0;
    // Eliminar comas de miles: "240,016,848" → 240016848
    const cleaned = String(val).replace(/,/g, '').trim();
    const n = Number(cleaned);
    return isNaN(n) ? 0 : n;
  };

  const parseStr = (val) => {
    if (val === undefined || val === null) return '';
    return String(val).trim();
  };

  const rows = facturas.map(f => ({
    // Usar los nombres exactos que aparecen en las cabeceras del Excel
    doc_num:      parseNum(f['DocNum']      ?? f['doc_num']      ?? f['N° Factura']),
    doc_date:     parseStr(f['DocDate']     ?? f['doc_date']     ?? f['Fecha']),
    card_code:    parseStr(f['CardCode']    ?? f['card_code']    ?? f['Cod. Cliente']),
    card_name:    parseStr(f['CardName']    ?? f['card_name']    ?? f['Cliente']),
    doc_total:    parseNum(f['DocTotal']    ?? f['doc_total']    ?? f['Total']),
    doc_currency: parseStr(f['DocCur']      ?? f['DocCurrency']  ?? f['doc_currency'] ?? 'PEN'),
    doc_status:   parseStr(f['DocStatus']   ?? f['doc_status']   ?? f['Estado']       ?? 'O'),
    created_at:   new Date().toISOString(),
  }));

  console.log('✅ Primera fila mapeada:', JSON.stringify(rows[0]));

  try {
    await bigquery
      .dataset('facturas_dataset')
      .table('facturas')
      .insert(rows);

    return res.json({ success: true, inserted: rows.length });
  } catch (err) {
    console.error('BigQuery insert error:', err);
    return res.status(500).json({ error: err.message });
  }
});


exports.queryFacturas = onRequest({
  cors: true,
  memory: "512MiB",
  timeoutSeconds: 120,
  secrets: [geminiApiKey, webApiKey]
}, async (req, res) => {
  res.set('Access-Control-Allow-Origin', '*');
  res.set('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  if (req.method === 'OPTIONS') return res.status(204).send('');
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  // ── Auth ──
  try {
    const token = req.headers.authorization?.replace('Bearer ', '');
    if (!token) return res.status(401).json({ error: 'No token' });
    await admin.auth().verifyIdToken(token);
  } catch {
    return res.status(401).json({ error: 'Token inválido' });
  }

  const { question } = req.body;
  if (!question) return res.status(400).json({ error: 'No se proporcionó pregunta' });

  const apiKey = geminiApiKey.value();
  if (!apiKey) return res.status(500).json({ error: 'API Key no configurada' });

  const TABLE = '`portfolio-app-9a4bc.facturas_dataset.facturas`';
  const SCHEMA = `
    Tabla BigQuery: ${TABLE}
    Columnas:
    - doc_num (INTEGER): número de factura
    - doc_date (TIMESTAMP): fecha de la factura
    - card_code (STRING): código del cliente
    - card_name (STRING): nombre del cliente
    - doc_total (FLOAT): monto total de la factura
    - doc_currency (STRING): moneda (S/ = soles, USD = dólares)
    - doc_status (STRING): estado (C = cerrada/pagada, O = abierta/pendiente)
    - created_at (TIMESTAMP): fecha de carga al sistema
  `;

  try {
    // ── Paso 1: Gemini genera SQL ──
    const genAI = new GoogleGenerativeAI(apiKey);
    const model = genAI.getGenerativeModel({ model: 'gemini-2.5-flash' });

    const sqlPrompt = `
Eres un experto en SQL para BigQuery.
Dado el siguiente schema de tabla:
${SCHEMA}

Genera SOLO la consulta SQL de BigQuery (sin explicaciones, sin markdown, sin \`\`\`) para responder esta pregunta:
"${question}"

Reglas:
- Usa solo BigQuery SQL estándar
- Para fechas usa FORMAT_TIMESTAMP o EXTRACT
- Limita resultados a máximo 50 filas con LIMIT
- Devuelve SOLO el SQL, nada más
    `.trim();

    const sqlResult = await model.generateContent(sqlPrompt);
    const sql = sqlResult.response.text().trim()
      .replace(/```sql/gi, '').replace(/```/g, '').trim();

    console.log('📝 SQL generado:', sql);

    // ── Paso 2: Ejecutar SQL en BigQuery ──
    const [rows] = await bigquery.query({ query: sql });
    console.log(`✅ BigQuery devolvió ${rows.length} filas`);

    // ── Paso 3: Gemini interpreta el resultado ──
    const interpretPrompt = `
El usuario preguntó: "${question}"

El resultado de la consulta a la base de datos fue:
${JSON.stringify(rows.slice(0, 20), null, 2)}

Responde al usuario en español de forma clara y concisa,
interpretando los datos. Si hay montos, menciona la moneda.
Si son muchos registros, resume los más relevantes.
    `.trim();

    const interpretResult = await model.generateContent(interpretPrompt);
    const answer = interpretResult.response.text();

    return res.json({
      success: true,
      question,
      sql,
      rowCount: rows.length,
      rows: rows.slice(0, 50),
      answer,
    });

  } catch (err) {
    console.error('❌ Error en queryFacturas:', err);
    return res.status(500).json({ error: err.message });
  }
});
