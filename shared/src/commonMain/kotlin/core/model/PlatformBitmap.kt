package core.model

/**
 * Abstracción multiplataforma para representar una imagen capturada.
 *
 * SOLID — DIP: El dominio depende de esta abstracción, no de implementaciones concretas.
 * Clean Architecture: core.model pertenece a la capa de dominio/core, no a presentation.
 *
 * Implementaciones:
 *  - Android  → actual typealias a android.graphics.Bitmap
 *  - iOS      → actual class vacía (stub, pendiente Vision framework)
 *  - Web      → actual class con imageData: String (base64 o Data URL)
 *  - Desktop  → actual class vacía (stub)
 */
expect class PlatformBitmap

