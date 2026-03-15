@echo off
REM Script para ejecutar la aplicación web en Windows

echo 🚀 Iniciando Portafolio Kotlin Web...

REM Compilar y ejecutar para WebAssembly
echo 📦 Compilando para WebAssembly...
call gradlew.bat :shared:wasmJsBrowserDevelopmentRun --continuous

echo ✅ Aplicación web disponible en: http://localhost:8080
echo 🌐 Navegador se abrirá automáticamente
echo 🔄 Modo desarrollo activo - Los cambios se recargarán automáticamente
pause
