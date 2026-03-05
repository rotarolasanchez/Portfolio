package data.repository

import core.interop.firebaseGetCurrentUser
import core.interop.firebaseSignIn
import core.interop.firebaseSignOut
import domain.model.UserModel
import domain.repositories.AuthRepository

/**
 * Implementación real de AuthRepository para Web usando Firebase Auth JS SDK
 */
class WebAuthRepositoryImpl : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
        return try {
            val jsonResult = firebaseSignIn(email, password)
            val user = parseUserJson(jsonResult)
            Result.success(user)
        } catch (e: Exception) {
            println("WebAuthRepository: Error signing in - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(): Result<UserModel> {
        return Result.failure(Exception("Google Sign-In no disponible en Web"))
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<UserModel> {
        return Result.failure(Exception("Sign Up no implementado en Web"))
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseSignOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): UserModel? {
        val json = firebaseGetCurrentUser() ?: return null
        return try {
            parseUserJson(json)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parsea un JSON simple del formato:
     * {"uid":"...","email":"...","displayName":"..."}
     */
    private fun parseUserJson(json: String): UserModel {
        val uid = extractJsonField(json, "uid")
        val email = extractJsonField(json, "email")
        val displayName = extractJsonField(json, "displayName")

        return UserModel(
            id = uid,
            email = email,
            userName = displayName.ifEmpty { email },
            userCode = email
        )
    }

    private fun extractJsonField(json: String, field: String): String {
        val key = "\"$field\":\""
        val startIndex = json.indexOf(key)
        if (startIndex == -1) return ""

        val valueStart = startIndex + key.length
        val valueEnd = json.indexOf("\"", valueStart)
        if (valueEnd == -1) return ""

        return json.substring(valueStart, valueEnd)
    }
}

