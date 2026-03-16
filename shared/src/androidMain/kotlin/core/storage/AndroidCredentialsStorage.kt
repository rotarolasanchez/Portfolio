package core.storage

import android.content.Context
import androidx.core.content.edit

/**
 * Implementación Android de CredentialsStorage usando SharedPreferences.
 * Los datos se guardan en el almacenamiento privado de la app.
 */
class AndroidCredentialsStorage(private val context: Context) : CredentialsStorage {

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun saveCredentials(email: String, password: String) {
        prefs.edit {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
        }
    }

    override fun loadCredentials(): SavedCredentials? {
        if (!isRememberEnabled()) return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val password = prefs.getString(KEY_PASSWORD, null) ?: return null
        return if (email.isNotEmpty() && password.isNotEmpty()) {
            SavedCredentials(email, password)
        } else null
    }

    override fun clearCredentials() {
        prefs.edit {
            remove(KEY_EMAIL)
            remove(KEY_PASSWORD)
        }
    }

    override fun isRememberEnabled(): Boolean =
        prefs.getBoolean(KEY_REMEMBER, false)

    override fun setRememberEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(KEY_REMEMBER, enabled) }
        if (!enabled) clearCredentials()
    }

    companion object {
        private const val PREFS_NAME = "auth_credentials"
        private const val KEY_EMAIL    = "saved_email"
        private const val KEY_PASSWORD = "saved_password"
        private const val KEY_REMEMBER = "remember_credentials"
    }
}
