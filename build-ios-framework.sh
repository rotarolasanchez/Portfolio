#!/bin/bash
# build-ios-framework.sh
#
# Script para compilar el framework iOS de Kotlin Multiplatform
# y copiarlo a la carpeta esperada por Xcode.
#
# Uso (desde la raíz del proyecto, en macOS):
#   chmod +x build-ios-framework.sh
#   ./build-ios-framework.sh [Debug|Release]
#
# Requisitos:
#   - macOS con Xcode instalado
#   - JDK 17+

CONFIGURATION=${1:-Debug}
echo "▶ Compilando framework iOS [$CONFIGURATION]..."

if [ "$CONFIGURATION" = "Release" ]; then
    ./gradlew \
        :shared:linkReleaseFrameworkIosArm64 \
        :shared:linkReleaseFrameworkIosSimulatorArm64 \
        :shared:linkReleaseFrameworkIosX64
else
    ./gradlew \
        :shared:linkDebugFrameworkIosArm64 \
        :shared:linkDebugFrameworkIosSimulatorArm64 \
        :shared:linkDebugFrameworkIosX64
fi

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar el framework iOS"
    exit 1
fi

echo "✅ Framework iOS compilado exitosamente"
echo ""
echo "📦 Frameworks disponibles en:"
echo "   shared/build/bin/iosSimulatorArm64/${CONFIGURATION,,}Framework/shared.framework  (Simulador M1/M2)"
echo "   shared/build/bin/iosArm64/${CONFIGURATION,,}Framework/shared.framework            (Dispositivo)"
echo "   shared/build/bin/iosX64/${CONFIGURATION,,}Framework/shared.framework              (Simulador Intel)"
echo ""
echo "📋 Pasos siguientes en Xcode:"
echo "   1. Abre iosApp/iosApp.xcodeproj"
echo "   2. En Build Settings > Framework Search Paths agrega:"
echo "      \$(SRCROOT)/../shared/build/bin/iosSimulatorArm64/${CONFIGURATION,,}Framework"
echo "   3. En General > Frameworks, Libraries > agrega shared.framework"
echo "   4. Compila y ejecuta en Simulator"


