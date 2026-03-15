#!/bin/bash

# Script para ejecutar la aplicación web
echo "🚀 Iniciando Portafolio Kotlin Web..."

# Compilar y ejecutar para WebAssembly
echo "📦 Compilando para WebAssembly..."
./gradlew :shared:wasmJsBrowserDevelopmentRun --continuous

echo "✅ Aplicación web disponible en: http://localhost:8080"
echo "🌐 Navegador se abrirá automáticamente"
echo "🔄 Modo desarrollo activo - Los cambios se recargarán automáticamente"

