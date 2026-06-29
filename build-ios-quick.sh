#!/bin/bash

# Quick iOS Build Script
# Facilita la compilación del framework iOS después de cambios

set -e

echo "🔄 Iniciando compilación del framework iOS..."
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Detectar arquitectura y configuración
ARCH="${1:-arm64}"  # arm64 para simulador M-series
CONFIG="${2:-Debug}" # Debug o Release

if [ "$ARCH" = "device" ]; then
    GRADLE_TASK="linkDebugFrameworkIosArm64"
    FRAMEWORK_PATH="shared/build/bin/iosArm64/debugFramework/shared.framework"
    echo -e "${BLUE}🎯 Compilando para dispositivo real (iOS arm64)${NC}"
elif [ "$ARCH" = "simulator-intel" ]; then
    GRADLE_TASK="linkDebugFrameworkIosX64"
    FRAMEWORK_PATH="shared/build/bin/iosX64/debugFramework/shared.framework"
    echo -e "${BLUE}🎯 Compilando para simulador Intel (x86_64)${NC}"
else
    GRADLE_TASK="linkDebugFrameworkIosSimulatorArm64"
    FRAMEWORK_PATH="shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework"
    echo -e "${BLUE}🎯 Compilando para simulador Apple Silicon (arm64)${NC}"
fi

# Release config if specified
if [ "$CONFIG" = "Release" ]; then
    GRADLE_TASK=$(echo $GRADLE_TASK | sed 's/Debug/Release/')
    FRAMEWORK_PATH=$(echo $FRAMEWORK_PATH | sed 's/debugFramework/releaseFramework/')
    echo -e "${YELLOW}📦 Configuración: Release${NC}"
else
    echo -e "${YELLOW}📦 Configuración: Debug${NC}"
fi

echo ""
echo -e "${BLUE}Running: ./gradlew :shared:$GRADLE_TASK${NC}"
echo ""

./gradlew ":shared:$GRADLE_TASK"

if [ -d "$FRAMEWORK_PATH" ]; then
    FRAMEWORK_SIZE=$(du -sh "$FRAMEWORK_PATH" | cut -f1)
    echo ""
    echo -e "${GREEN}✅ Framework compilado exitosamente!${NC}"
    echo ""
    echo -e "${BLUE}📍 Ubicación:${NC}"
    echo "   $FRAMEWORK_PATH"
    echo ""
    echo -e "${BLUE}📊 Tamaño:${NC}"
    echo "   $FRAMEWORK_SIZE"
    echo ""
    echo -e "${BLUE}📋 Próximo paso en Xcode:${NC}"
    echo "   General → Frameworks → Add → $FRAMEWORK_PATH"
    echo ""
    echo -e "${GREEN}🚀 Listo para integrar en Xcode${NC}"
else
    echo ""
    echo -e "${YELLOW}⚠️  Advertencia: Framework no encontrado en la ruta esperada${NC}"
    echo "   Verifica los logs anteriores para más detalles"
    exit 1
fi

