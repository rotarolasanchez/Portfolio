package core.storage

/**
 * Implementación Web/WasmJs de CredentialsStorage usando localStorage del navegador.
 */
class WebCredentialsStorage : CredentialsStorage {

    override fun saveCredentials(email: String, password: String) {
        localStorageSetItem(KEY_EMAIL, email)
        localStorageSetItem(KEY_PASSWORD, password)
    }

    override fun loadCredentials(): SavedCredentials? {
        if (!isRememberEnabled()) return null
        val email = localStorageGetItem(KEY_EMAIL) ?: return null
        val password = localStorageGetItem(KEY_PASSWORD) ?: return null
        return if (email.isNotEmpty() && password.isNotEmpty()) {
            SavedCredentials(email, password)
        } else null
    }

    override fun clearCredentials() {
        localStorageRemoveItem(KEY_EMAIL)
        localStorageRemoveItem(KEY_PASSWORD)
    }

    override fun isRememberEnabled(): Boolean =
        localStorageGetItem(KEY_REMEMBER) == "true"

    override fun setRememberEnabled(enabled: Boolean) {
        localStorageSetItem(KEY_REMEMBER, if (enabled) "true" else "false")
        if (!enabled) clearCredentials()
    }

    companion object {
        private const val KEY_EMAIL    = "auth_saved_email"
        private const val KEY_PASSWORD = "auth_saved_password"
        private const val KEY_REMEMBER = "auth_remember_me"
    }
}

// --- Interop con window.localStorage ---
@JsFun("(key, value) => { try { window.localStorage.setItem(key, value); } catch(e) {} }")
private external fun localStorageSetItem(key: String, value: String)

@JsFun("(key) => { try { return window.localStorage.getItem(key); } catch(e) { return null; } }")
private external fun localStorageGetItem(key: String): String?

@JsFun("(key) => { try { window.localStorage.removeItem(key); } catch(e) {} }")
private external fun localStorageRemoveItem(key: String)


