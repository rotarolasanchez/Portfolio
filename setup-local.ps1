# =============================================================================
# setup-local.ps1 - Script de configuración del entorno de desarrollo local
# =============================================================================
# Este script prepara los archivos sensibles necesarios para compilar localmente.
# Uso: .\setup-local.ps1
# =============================================================================

Write-Host "=== Configuración del entorno local ===" -ForegroundColor Cyan

# --- 1. google-services.json ---
$googleServicesPath = "app\google-services.json"
$googleServicesBase64Path = "app\google-services-base64.txt"

if (Test-Path $googleServicesPath) {
    Write-Host "[OK] google-services.json ya existe en app/" -ForegroundColor Green
} elseif (Test-Path $googleServicesBase64Path) {
    Write-Host "[INFO] Decodificando google-services.json desde base64..." -ForegroundColor Yellow
    $base64Content = Get-Content $googleServicesBase64Path -Raw
    $bytes = [Convert]::FromBase64String($base64Content.Trim())
    [System.IO.File]::WriteAllBytes((Resolve-Path "app\") + "google-services.json", $bytes)
    Write-Host "[OK] google-services.json creado en app/" -ForegroundColor Green
} else {
    Write-Host "[ERROR] google-services.json NO encontrado." -ForegroundColor Red
    Write-Host ""
    Write-Host "  Opciones:" -ForegroundColor Yellow
    Write-Host "  1. Descárgalo de Firebase Console:"
    Write-Host "     https://console.firebase.google.com -> tu proyecto -> Configuracion -> Your apps -> Android"
    Write-Host "     y colócalo en: app\google-services.json"
    Write-Host ""
    Write-Host "  2. O guarda el contenido base64 en: app\google-services-base64.txt"
    Write-Host "     y vuelve a ejecutar este script."
    Write-Host ""
}

# --- 2. keystore.properties y key.jks ---
$keystorePropsPath = "app\keystore.properties"
$keystoreBase64Path = "app\keystore_properties_base64.txt"

if (Test-Path $keystorePropsPath) {
    Write-Host "[OK] keystore.properties ya existe en app/" -ForegroundColor Green
} elseif (Test-Path $keystoreBase64Path) {
    Write-Host "[INFO] Decodificando keystore.properties desde base64..." -ForegroundColor Yellow
    $base64Content = Get-Content $keystoreBase64Path -Raw
    # Eliminar espacios del base64 (el archivo tiene espacios entre caracteres)
    $cleanBase64 = ($base64Content -replace '\s', '')
    try {
        $bytes = [Convert]::FromBase64String($cleanBase64)
        $text = [System.Text.Encoding]::UTF8.GetString($bytes)
        Set-Content -Path $keystorePropsPath -Value $text
        Write-Host "[OK] keystore.properties creado en app/" -ForegroundColor Green
    } catch {
        Write-Host "[WARN] No se pudo decodificar keystore_properties_base64.txt. Verifica el formato." -ForegroundColor Yellow
    }
} else {
    Write-Host "[WARN] keystore.properties no encontrado (necesario para builds release)" -ForegroundColor Yellow
}

# --- 3. local.properties ---
$localPropsPath = "local.properties"
if (Test-Path $localPropsPath) {
    Write-Host "[OK] local.properties ya existe" -ForegroundColor Green
    # Verificar que tiene las claves necesarias
    $content = Get-Content $localPropsPath -Raw
    if ($content -notmatch "GEMINI_API_KEY") {
        Write-Host "[WARN] local.properties no tiene GEMINI_API_KEY definida" -ForegroundColor Yellow
        Write-Host "       Agrega: GEMINI_API_KEY=tu_clave_aqui" -ForegroundColor Yellow
    }
} else {
    Write-Host "[WARN] local.properties no encontrado. Creando plantilla..." -ForegroundColor Yellow
    $sdkDir = $env:ANDROID_HOME
    if (-not $sdkDir) {
        $sdkDir = "$env:LOCALAPPDATA\Android\Sdk"
    }
    $template = @"
sdk.dir=$($sdkDir -replace '\\', '/')
GEMINI_API_KEY=your_gemini_api_key_here
MODEL_NAME=gemini-1.5-pro
"@
    Set-Content -Path $localPropsPath -Value $template
    Write-Host "[OK] Plantilla local.properties creada. Edítala con tus claves reales." -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Setup completado ===" -ForegroundColor Cyan
Write-Host "Ahora puedes compilar con: .\gradlew :app:assembleDebug" -ForegroundColor White

