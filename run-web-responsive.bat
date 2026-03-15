@echo off
REM Script mejorado para ejecutar la aplicación web responsiva

echo 🚀 Iniciando Portafolio Kotlin Web - Versión Responsiva...
echo.

REM Limpiar cache de Gradle primero
echo 📦 Limpiando cache de compilación...
call gradlew.bat clean --no-daemon --quiet

REM Compilar para WebAssembly
echo 📱 Compilando para WebAssembly con optimizaciones responsivas...
call gradlew.bat :shared:wasmJsBrowserDevelopmentRun --continuous --no-daemon

echo.
echo ✅ Aplicación web responsiva disponible en: http://localhost:8080
echo 📱 Optimizada para móvil, tablet y desktop
echo 🌐 El navegador se abrirá automáticamente
echo 🔄 Modo desarrollo activo - Los cambios se recargarán automáticamente
echo.
echo 💡 Tip: Prueba la responsividad redimensionando la ventana del navegador
echo 📱 O abre las herramientas de desarrollador (F12) para probar diferentes dispositivos
echo.
pause
