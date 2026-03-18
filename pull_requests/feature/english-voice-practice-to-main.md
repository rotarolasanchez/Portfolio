## 📱 English Voice Practice Feature

Esta PR agrega una nueva funcionalidad de práctica de inglés por voz usando Gemini AI.

## 📋 Archivos incluidos

| Archivo | Descripción |
|---|---|
| `EnglishPracticeUiState.kt` | Estado de UI para la pantalla de práctica |
| `EnglishPracticeViewModel.kt` | ViewModel con lógica de SpeechRecognizer, TTS y Gemini AI |
| `VoiceButtonAtom.kt` | Botón animado de voz con estados visuales |
| `VoiceChatOrganism.kt` | Organismo principal del chat de voz |
| `EnglishPracticePage.kt` | Pantalla completa de práctica de inglés |
| `AndroidManifest.xml` | Permisos de micrófono y audio agregados |
| `.github/workflows/android_ci.yml` | CI workflow para compilar y testear la app |

## ✅ Funcionalidades

- 🎙️ Reconocimiento de voz en inglés (SpeechRecognizer)
- 🤖 Respuestas de Gemini AI como tutor de inglés
- 🔊 Text-to-Speech para escuchar las respuestas
- 💬 3 modos de práctica: Conversación libre, Entrevista técnica, Vocabulario
- 🔴 Animación pulsante mientras escucha
- 📊 CI automático con GitHub Actions

## 🧪 CI/CD
El workflow de GitHub Actions compilará automáticamente el APK debug al hacer merge de esta PR.