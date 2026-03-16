package core.storage

import platform.Foundation.NSUserDefaults

/**
 * Implementación iOS de CredentialsStorage usando NSUserDefaults.
 */
class IosCredentialsStorage : CredentialsStorage {

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun saveCredentials(email: String, password: String) {
        defaults.setObject(email, KEY_EMAIL)
        defaults.setObject(password, KEY_PASSWORD)
        defaults.synchronize()
    }

    override fun loadCredentials(): SavedCredentials? {
        if (!isRememberEnabled()) return null
        val email = defaults.stringForKey(KEY_EMAIL) ?: return null
        val password = defaults.stringForKey(KEY_PASSWORD) ?: return null
        return if (email.isNotEmpty() && password.isNotEmpty()) {
            SavedCredentials(email, password)
        } else null
    }

    override fun clearCredentials() {
        defaults.removeObjectForKey(KEY_EMAIL)
        defaults.removeObjectForKey(KEY_PASSWORD)
        defaults.synchronize()
    }

    override fun isRememberEnabled(): Boolean =
        defaults.boolForKey(KEY_REMEMBER)

    override fun setRememberEnabled(enabled: Boolean) {
        defaults.setBool(enabled, KEY_REMEMBER)
        defaults.synchronize()
        if (!enabled) clearCredentials()
    }

    companion object {
        private const val KEY_EMAIL    = "auth_saved_email"
        private const val KEY_PASSWORD = "auth_saved_password"
        private const val KEY_REMEMBER = "auth_remember_me"
    }
}

