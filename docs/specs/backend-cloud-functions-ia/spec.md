# Spec: Backend Cloud Functions de IA y facturas

> Metodología: Spec-First | Estado: Completo | Fecha: 2026-11-20

---

## 1. Visión del Producto

Funciones HTTP de Firebase que protegen con autenticación el acceso a Gemini, a BigQuery (facturas y cubo de ventas) y a un agente Ollama local.

**Problema que resuelve:** Exponer de forma segura y sin claves en el cliente los servicios de IA y los datos de facturación.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| App cliente | Llama a `askGemini` con mensaje, historial o imagen | Respuesta de Gemini |
| Admin web | Envía filas a `uploadCuboVentas` o `uploadExcelToBigQuery` | Filas insertadas en BigQuery |
| App o admin | Llama a `queryFacturas` con una pregunta | Respuesta en lenguaje natural sobre BigQuery |
| App cliente | Llama a `queryOllama` | Respuesta del agente local vía túnel |

**Usuario principal:** App cliente (móvil/web) y panel admin que consumen las funciones

---

## 3. Funcionalidades

### En scope ✅
- `askGemini`: verifica un Bearer token y, si falla, hace fallback con email y contraseña vía REST de Firebase; acepta historial e imagen base64; reintenta con otros modelos si Gemini está saturado
- `uploadCuboVentas`: valida token y el arreglo `ventas`, normaliza números y textos e inserta en BigQuery
- `uploadExcelToBigQuery`: valida token y `facturas` e inserta en `facturas_dataset.facturas` con alias de columnas
- `queryFacturas`: valida token y pregunta, y consulta BigQuery con Gemini
- `queryOllama`: proxy autenticado hacia el túnel `OLLAMA_TUNNEL_URL` (`/query/`) con timeout de 270 s, que devuelve `respuesta` y `sql_generado`
- CORS, manejo de OPTIONS y de método no permitido (405), secretos con `defineSecret`
- Script auxiliar de creación de tabla (`create_cubo_ventas_table.js`)

### Fuera de scope ❌
- Autorización por roles (cualquier usuario autenticado accede)
- Rate limiting propio
- Despliegue del servidor Ollama/FastAPI

---

## 4. Requisitos No Funcionales

backend-cloud-functions-ia0

---

## 5. Criterios de Éxito

backend-cloud-functions-ia1

---

## 7. Archivos implementados

backend-cloud-functions-ia2
