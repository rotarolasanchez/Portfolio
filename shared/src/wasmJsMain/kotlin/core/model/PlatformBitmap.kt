package core.model

/**
 * Web (WasmJs): PlatformBitmap wrappea una imagen como base64/Data URL.
 * Se usa para enviar imágenes adjuntas al servicio de análisis.
 */
actual class PlatformBitmap {
    var imageData: String? = null

    constructor(data: String) {
        this.imageData = data
    }

    constructor()
}

