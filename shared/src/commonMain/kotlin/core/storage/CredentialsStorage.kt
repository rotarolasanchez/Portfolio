package core.storage

/**
 * Interfaz KMP para almacenar credenciales de forma persistente.
 * Cada plataforma provee su propia implementación via Koin.
 */
interface CredentialsStorage {
    /** Guarda email y contraseña */
    fun saveCredentials(email: String, password: String)

    /** Carga las credenciales guardadas. Retorna null si no hay ninguna. */
    fun loadCredentials(): SavedCredentials?

    /** Borra las credenciales guardadas */
    fun clearCredentials()

    /** Indica si el usuario marcó "Recordar contraseña" */
    fun isRememberEnabled(): Boolean

    /** Activa o desactiva la opción "Recordar contraseña" */
    fun setRememberEnabled(enabled: Boolean)
}

data class SavedCredentials(
    val email: String,
    val password: String
)

