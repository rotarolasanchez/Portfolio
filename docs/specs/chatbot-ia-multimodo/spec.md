# Spec: Chatbot de IA multimodo y análisis de imágenes

> Metodología: Spec-First | Estado: Completo | Fecha: 2026-11-20

---

## 1. Visión del Producto

Chat con IA dentro de la app con tres modos (libre con Gemini, agente comercial sobre BigQuery y Ollama local), más análisis de imágenes capturadas con la cámara.

**Problema que resuelve:** Consultar IA generativa y datos de facturas en lenguaje natural, y resolver problemas a partir de una foto.

---

## 2. Usuarios y Casos de Uso

| Usuario | Acción | Resultado esperado |
| --- | --- | --- |
| Usuario autenticado | Envía un mensaje en modo Libre | Respuesta de Gemini con el historial de la conversación |
| Usuario autenticado | Envía una pregunta en modo Agente | Respuesta basada en facturas de BigQuery |
| Usuario autenticado | Envía una pregunta en modo Ollama | Respuesta del agente local con IA |
| Usuario autenticado | Captura o selecciona una imagen | La imagen se analiza (OCR/Gemini) y se resuelve el problema |

**Usuario principal:** Usuario autenticado de la app

---

## 3. Funcionalidades

### En scope ✅
- Tres modos de chat (`ChatMode.LIBRE`, `AGENTE`, `OLLAMA`) con cambio directo por `setMode` y mensaje informativo al cambiar
- Envío de mensajes (`sendMessage`): ignora texto vacío, agrega el mensaje del usuario, enruta al caso de uso según el modo y muestra el error como mensaje
- Análisis de imagen (`AnalyzeImageUseCase`) y resolución de problemas (`SolveProblemUseCase`) desde `processImage`
- Control de cámara (`showCamera`, `hideCamera`), `reset` y `clearError`
- Contrato `ChatBotRepository` con implementaciones Android, iOS y Web
- Servicios de Gemini y reconocimiento de texto (ML Kit en Android)
- UI por capas (átomos, moléculas, organismos, plantilla, página) con variantes por plataforma

### Fuera de scope ❌
- Persistencia del historial de conversaciones
- Streaming de respuestas
- Cámara en Desktop más allá de lo implementado en su `Camera.kt`

---

## 4. Requisitos No Funcionales

chatbot-ia-multimodo0

---

## 5. Criterios de Éxito

chatbot-ia-multimodo1

---

## 7. Archivos implementados

chatbot-ia-multimodo2
