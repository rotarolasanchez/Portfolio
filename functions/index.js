const { GoogleGenerativeAI } = require("@google/generative-ai");
const { onRequest } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const admin = require("firebase-admin");
const https = require("https");

admin.initializeApp();

// Secretos de Cloud Functions
const geminiApiKey = defineSecret("GEMINI_API_KEY");
const webApiKey    = defineSecret("WEB_API_KEY");
const ollamaUrl    = defineSecret("OLLAMA_TUNNEL_URL"); // URL base del túnel Cloudflare

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
    "gemini-2.0-flash",
    "gemini-2.0-flash-lite",
  ];
  const sleep = (ms) => new Promise(r => setTimeout(r, ms));

  for (let attempt = 0; attempt < 3; attempt++) {
    for (const modelName of models) {
      try {
        console.log(`🤖 Intentando con modelo: ${modelName} (intento ${attempt + 1})`);
        const genAI = new GoogleGenerativeAI(apiKey);
        const model = genAI.getGenerativeModel({ model: modelName });
        const result = await model.generateContent(prompt);
        console.log(`✅ Respuesta recibida con modelo: ${modelName}`);
        return result.response.text();
      } catch (error) {
        const is404 = error.message?.includes('404') || error.message?.includes('not found');
        console.warn(`⚠️ Falló modelo ${modelName}: ${error.message}`);
        if (is404) continue;
        if (modelName === models[models.length - 1] && attempt < 2) {
          await sleep(3000);
        }
      }
    }
  }
  throw new Error('Todos los modelos de Gemini están saturados. Intenta en unos segundos.');
}

async function callGeminiWithImage(apiKey, prompt, imageBase64, mimeType) {
  const models = [
    "gemini-2.5-flash",
    "gemini-2.0-flash",
    "gemini-2.0-flash-lite",
  ];
  const sleep = (ms) => new Promise(r => setTimeout(r, ms));

  for (let attempt = 0; attempt < 3; attempt++) {
    for (const modelName of models) {
      try {
        console.log(`🤖 Intentando multimodal con modelo: ${modelName} (intento ${attempt + 1})`);
        const genAI = new GoogleGenerativeAI(apiKey);
        const model = genAI.getGenerativeModel({ model: modelName });
        const imagePart = { inlineData: { data: imageBase64, mimeType } };
        const result = await model.generateContent([prompt, imagePart]);
        console.log(`✅ Respuesta multimodal recibida con modelo: ${modelName}`);
        return result.response.text();
      } catch (error) {
        const is404 = error.message?.includes('404') || error.message?.includes('not found');
        console.warn(`⚠️ Falló modelo multimodal ${modelName}: ${error.message}`);
        if (is404) continue;
        if (modelName === models[models.length - 1] && attempt < 2) {
          await sleep(3000);
        }
      }
    }
  }
  throw new Error('Todos los modelos de Gemini están saturados. Intenta en unos segundos.');
}



exports.uploadCuboVentas = onRequest({
  cors: true,
  memory: "512MiB",
  timeoutSeconds: 300,
}, async (req, res) => {
  res.set('Access-Control-Allow-Origin', '*');
  res.set('Access-Control-Allow-Methods', 'POST, OPTIONS');
  res.set('Access-Control-Allow-Headers', 'Content-Type, Authorization');
  if (req.method === 'OPTIONS') return res.status(204).send('');
  if (req.method !== 'POST') return res.status(405).json({ error: 'Method not allowed' });

  try {
    const token = req.headers.authorization?.replace('Bearer ', '');
    if (!token) return res.status(401).json({ error: 'No token' });
    await admin.auth().verifyIdToken(token);
  } catch {
    return res.status(401).json({ error: 'Token inválido' });
  }

  const { ventas } = req.body;
  if (!Array.isArray(ventas) || ventas.length === 0) {
    return res.status(400).json({ error: 'No hay datos para insertar' });
  }

  console.log('🔍 Primera fila recibida:', JSON.stringify(ventas[0]));

  const parseNum = (val) => {
    if (val === undefined || val === null || val === '') return 0;
    const cleaned = String(val).replace(/,/g, '').trim();
    const n = Number(cleaned);
    return isNaN(n) ? 0 : n;
  };
  const parseStr = (val) => (val !== undefined && val !== null ? String(val).trim() : '');
  const parseInt2 = (val) => Math.round(parseNum(val));

  const rows = ventas.map(f => ({
    fecha_emision:        parseStr(f['FechaEmision']),
    documento:            parseStr(f['Documento']),
    tipo_venta:           parseStr(f['TipoVenta']),
    termino_pago_resumen: parseStr(f['TerminoPago_Resumen']),
    documento_id:         parseStr(f['Documento_ID']),
    moneda:               parseStr(f['Moneda']),
    tipo_cambio:          parseNum(f['Tipo_Cambio']),
    precio:               parseNum(f['Precio']),
    almacen:              parseStr(f['Almacen']),
    anio:                 parseInt2(f['Anio']),
    mes_texto:            parseStr(f['Mes_Texto']),
    dia_num:              parseInt2(f['Dia_Num']),
    numero_legal:         parseStr(f['NumeroLegal']),
    fecha_vencimiento:    parseStr(f['FechaVencimiento']),
    dias_vencimiento:     parseInt2(f['DiasVencimiento']),
    fise:                 parseStr(f['FISE']),
    precio_antes_dscto:   parseNum(f['PrecioAntesDscto']),
    sucursal:             parseStr(f['Sucursal']),
    producto_id:          parseStr(f['Producto_ID']),
    unidad_medida:        parseStr(f['UnidadMedida']),
    producto:             parseStr(f['Producto']),
    categoria:            parseStr(f['Categoria']),
    familia:              parseStr(f['Familia']),
    sub_familia:          parseStr(f['SubFamilia']),
    linea:                parseStr(f['Linea']),
    marca:                parseStr(f['Marca']),
    grupo_producto:       parseStr(f['GrupoProducto']),
    cliente_id:           parseStr(f['Cliente_ID']),
    cliente:              parseStr(f['Cliente']),
    zona_id:              parseStr(f['Zona_ID']),
    zona:                 parseStr(f['Zona']),
    cliente_categoria:    parseStr(f['Cliente_Categoria']),
    distrito:             parseStr(f['Distrito']),
    provincia:            parseStr(f['Provincia']),
    departamento:         parseStr(f['Departamento']),
    pais:                 parseStr(f['Pais']),
    vendedor:             parseStr(f['Vendedor']),
    analista:             parseStr(f['Analista']),
    supervisor:           parseStr(f['Supervisor']),
    gerencia:             parseStr(f['Gerencia']),
    unidad_negocio:       parseStr(f['UnidadNegocio']),
    grupo_unidad_negocio: parseStr(f['GrupoUnidadNegocio']),
    tipo_gerencia:        parseStr(f['TipoGerencia']),
    procedencia:          parseStr(f['Procedencia']),
    total_con_impuesto:   parseNum(f['Total_con_Impuesto']),
    galones:              parseNum(f['Galones']),
    total_costo:          parseNum(f['Total_Costo']),
    total_sin_impuesto:   parseNum(f['Total_sin_Impuesto']),
    cantidad:             parseNum(f['Cantidad']),
    descuento:            parseNum(f['Descuento']),
    descuento_financiero: parseNum(f['DescuentoFinanciero']),
    descuento_porc:       parseNum(f['Descuento_Porc']),
    otros_descuentos:     parseNum(f['Otros_Descuentos']),
    barriles:             parseNum(f['Barriles']),
    created_at:           new Date().toISOString(),
  }));

  console.log('✅ Primera fila mapeada:', JSON.stringify(rows[0]));

  // Schema de la tabla cubo_ventas
  const cuboSchema = [
    { name: 'fecha_emision',        type: 'STRING' },
    { name: 'documento',            type: 'STRING' },
    { name: 'tipo_venta',           type: 'STRING' },
    { name: 'termino_pago_resumen', type: 'STRING' },
    { name: 'documento_id',         type: 'STRING' },
    { name: 'moneda',               type: 'STRING' },
    { name: 'tipo_cambio',          type: 'FLOAT64' },
    { name: 'precio',               type: 'FLOAT64' },
    { name: 'almacen',              type: 'STRING' },
    { name: 'anio',                 type: 'INT64' },
    { name: 'mes_texto',            type: 'STRING' },
    { name: 'dia_num',              type: 'INT64' },
    { name: 'numero_legal',         type: 'STRING' },
    { name: 'fecha_vencimiento',    type: 'STRING' },
    { name: 'dias_vencimiento',     type: 'INT64' },
    { name: 'fise',                 type: 'STRING' },
    { name: 'precio_antes_dscto',   type: 'FLOAT64' },
    { name: 'sucursal',             type: 'STRING' },
    { name: 'producto_id',          type: 'STRING' },
    { name: 'unidad_medida',        type: 'STRING' },
    { name: 'producto',             type: 'STRING' },
    { name: 'categoria',            type: 'STRING' },
    { name: 'familia',              type: 'STRING' },
    { name: 'sub_familia',          type: 'STRING' },
    { name: 'linea',                type: 'STRING' },
    { name: 'marca',                type: 'STRING' },
    { name: 'grupo_producto',       type: 'STRING' },
    { name: 'cliente_id',           type: 'STRING' },
    { name: 'cliente',              type: 'STRING' },
    { name: 'zona_id',              type: 'STRING' },
    { name: 'zona',                 type: 'STRING' },
    { name: 'cliente_categoria',    type: 'STRING' },
    { name: 'distrito',             type: 'STRING' },
    { name: 'provincia',            type: 'STRING' },
    { name: 'departamento',         type: 'STRING' },
    { name: 'pais',                 type: 'STRING' },
    { name: 'vendedor',             type: 'STRING' },
    { name: 'analista',             type: 'STRING' },
    { name: 'supervisor',           type: 'STRING' },
    { name: 'gerencia',             type: 'STRING' },
    { name: 'unidad_negocio',       type: 'STRING' },
    { name: 'grupo_unidad_negocio', type: 'STRING' },
    { name: 'tipo_gerencia',        type: 'STRING' },
    { name: 'procedencia',          type: 'STRING' },
    { name: 'total_con_impuesto',   type: 'FLOAT64' },
    { name: 'galones',              type: 'FLOAT64' },
    { name: 'total_costo',          type: 'FLOAT64' },
    { name: 'total_sin_impuesto',   type: 'FLOAT64' },
    { name: 'cantidad',             type: 'FLOAT64' },
    { name: 'descuento',            type: 'FLOAT64' },
    { name: 'descuento_financiero', type: 'FLOAT64' },
    { name: 'descuento_porc',       type: 'FLOAT64' },
    { name: 'otros_descuentos',     type: 'FLOAT64' },
    { name: 'barriles',             type: 'FLOAT64' },
    { name: 'created_at',           type: 'TIMESTAMP' },
  ];

  try {
    const dataset = bigquery.dataset('facturas_dataset');
    const table   = dataset.table('cubo_ventas');

    // Crear tabla automáticamente si no existe
    const [exists] = await table.exists();
    if (!exists) {
      console.log('📦 Tabla cubo_ventas no existe, creando...');
      await dataset.createTable('cubo_ventas', { schema: cuboSchema });
      console.log('✅ Tabla cubo_ventas creada.');
    }
    // Insertar en lotes de 500 para evitar timeout
    const BATCH = 500;
    for (let i = 0; i < rows.length; i += BATCH) {
      await bigquery
        .dataset('facturas_dataset')
        .table('cubo_ventas')
        .insert(rows.slice(i, i + BATCH));
    }
    return res.json({ success: true, inserted: rows.length });
  } catch (err) {
    console.error('BigQuery insert error:', err?.errors ?? err);
    const msg = err?.errors?.[0]?.errors?.[0]?.message ?? err.message;
    return res.status(500).json({ error: msg });
  }
});

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

  /*const TABLE = '`portfolio-app-9a4bc.facturas_dataset.facturas`';
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
  `;*/
  // ✅ NUEVO — apunta a cubo_ventas
  const TABLE = '`portfolio-app-9a4bc.facturas_dataset.cubo_ventas`';
  const SCHEMA = `
    Tabla BigQuery: ${TABLE}
    Columnas principales:
    - fecha_emision (STRING): fecha de emisión del documento
    - anio (INTEGER): año de la venta
    - mes_texto (STRING): mes en texto (Enero, Febrero, etc.)
    - moneda (STRING): moneda (PEN = soles, USD = dólares)
    - tipo_cambio (FLOAT): tipo de cambio aplicado
    - numero_legal (STRING): número legal de la factura
    - fecha_vencimiento (STRING): fecha de vencimiento
    - dias_vencimiento (INTEGER): días hasta el vencimiento
    - sucursal (STRING): sucursal de venta
    - producto_id (STRING): código del producto
    - producto (STRING): nombre del producto
    - unidad_medida (STRING): unidad de medida
    - categoria (STRING): categoría del producto
    - familia (STRING): familia del producto
    - sub_familia (STRING): subfamilia del producto
    - linea (STRING): línea del producto
    - marca (STRING): marca del producto
    - grupo_producto (STRING): grupo del producto
    - cliente_id (STRING): código del cliente
    - cliente (STRING): nombre del cliente
    - zona (STRING): zona del cliente
    - cliente_categoria (STRING): categoría del cliente
    - distrito (STRING): distrito
    - departamento (STRING): departamento
    - vendedor (STRING): vendedor responsable
    - supervisor (STRING): supervisor
    - gerencia (STRING): gerencia
    - unidad_negocio (STRING): unidad de negocio
    - total_con_impuesto (FLOAT): total con impuesto
    - total_sin_impuesto (FLOAT): total sin impuesto
    - total_costo (FLOAT): costo total
    - cantidad (FLOAT): cantidad vendida
    - galones (FLOAT): cantidad en galones
    - precio (FLOAT): precio unitario
    - precio_antes_dscto (FLOAT): precio antes de descuento
    - descuento (FLOAT): descuento aplicado
    - descuento_porc (FLOAT): porcentaje de descuento
    - tipo_venta (STRING): tipo de venta
    - termino_pago_resumen (STRING): término de pago
    - procedencia (STRING): procedencia del pedido
    - created_at (TIMESTAMP): fecha de carga al sistema
  `;

  const MODELS = [
    'gemini-2.5-flash',
    'gemini-2.0-flash',
    'gemini-2.0-flash-lite',
  ];

  const sleep = (ms) => new Promise(r => setTimeout(r, ms));

  async function generateWithFallback(prompt) {
    for (let attempt = 0; attempt < 3; attempt++) {
      for (const modelName of MODELS) {
        try {
          console.log(`🤖 queryFacturas intento ${attempt + 1} modelo: ${modelName}`);
          const genAI = new GoogleGenerativeAI(apiKey);
          const m = genAI.getGenerativeModel({ model: modelName });
          const result = await m.generateContent(prompt);
          console.log(`✅ queryFacturas OK con modelo: ${modelName}`);
          return result.response.text();
        } catch (err) {
          const is503 = err.message?.includes('503') || err.message?.includes('high demand') || err.message?.includes('Service Unavailable');
          const is404 = err.message?.includes('404') || err.message?.includes('not found');
          console.warn(`⚠️ Falló ${modelName} (intento ${attempt + 1}): ${err.message}`);
          if (is404) continue; // modelo no existe, probar siguiente
          if (is503 && modelName === MODELS[MODELS.length - 1] && attempt < 2) {
            console.log(`⏳ Todos saturados, esperando 3s antes de reintentar...`);
            await sleep(3000);
          }
        }
      }
    }
    throw new Error('Todos los modelos de Gemini están temporalmente saturados. Intenta en unos segundos.');
  }

  try {
    // ── Paso 1: Gemini genera SQL (con fallback de modelos) ──
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

    const sqlResult = await generateWithFallback(sqlPrompt);
    const sql = sqlResult.trim()
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

    // ── Paso 3: Gemini interpreta el resultado (con fallback) ──
    const interpretResult = await generateWithFallback(interpretPrompt);
    const answer = interpretResult;

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

// ─────────────────────────────────────────────────────────────────────────────
// queryOllama — Proxy seguro hacia el servidor FastAPI/Ollama local
// El móvil llama aquí con Bearer token → Cloud Function llama al túnel Cloudflare
// Para cambiar la URL del túnel: firebase functions:secrets:set OLLAMA_TUNNEL_URL
// ─────────────────────────────────────────────────────────────────────────────
exports.queryOllama = onRequest({
  cors: true,
  memory: "512MiB",
  timeoutSeconds: 300,
  secrets: [ollamaUrl],
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

  const tunnelBase = (ollamaUrl.value() || '').trim().replace(/\/$/, '');
  if (!tunnelBase) {
    return res.status(500).json({ error: 'Servidor Ollama no configurado. Ejecuta: firebase functions:secrets:set OLLAMA_TUNNEL_URL' });
  }

  const targetUrl = `${tunnelBase}/query/`;
  console.log(`🦙 queryOllama proxy → ${targetUrl}`);

  try {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), 270_000); // 4.5 min

    const ollamaRes = await fetch(targetUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'ngrok-skip-browser-warning': 'true',
        'User-Agent': 'FirebaseCloudFunction/1.0',
      },
      body: JSON.stringify({ pregunta: question }),
      signal: controller.signal,
    });
    clearTimeout(timer);

    const data = await ollamaRes.json();

    if (!ollamaRes.ok) {
      console.error(`❌ Error del agente HTTP ${ollamaRes.status}:`, data);
      return res.status(ollamaRes.status).json({ error: data?.detail ?? 'Error en el agente Ollama' });
    }

    console.log('✅ Respuesta del agente recibida');
    return res.json({
      success: true,
      respuesta:    data.respuesta    ?? '',
      sql_generado: data.sql_generado ?? '',
    });

  } catch (err) {
    console.error('❌ Error llamando al agente:', err.message);
    const isTimeout = err.name === 'AbortError';
    return res.status(isTimeout ? 504 : 500).json({
      error: isTimeout
        ? 'El agente tardó demasiado. Intenta con una pregunta más específica.'
        : err.message,
    });
  }
});

