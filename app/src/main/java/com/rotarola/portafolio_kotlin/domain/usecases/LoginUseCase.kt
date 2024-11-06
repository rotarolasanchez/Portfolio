package com.rotarola.portafolio_kotlin.domain.usecases

import android.util.Log
import com.rotarola.portafolio_kotlin.domain.entities.User
import com.rotarola.portafolio_kotlin.data.model.RequestState
import com.rotarola.portafolio_kotlin.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    fun geUsersApp(code: String, password: String): Flow<RequestState<List<User>>> {
        Log.e("LoginUseCase", "geUsersApp: $code $password")
        return flow {
            Log.e(
                "LoginUseCase",
                "geUsersApp: isUserValid(code, password): " + isUserValid(code, password)
            )
            if (isUserValid(code, password)) {
                try {
                    val users = userRepository.geUsersApp(code, password)
                    if (users.last().size > 0) {
                        emit(RequestState.Success(users.last()))
                    } else {
                        emit(RequestState.Error(Exception("Usuario no existe")))
                    }
                } catch (e: Exception) {
                    emit(RequestState.Error(e))
                }
            } else {
                emit(RequestState.Error(Exception("Usuario no valido")))
            }
        }
    }

    fun isUserValid(code: String?, password: String?): Boolean {
        return !code.isNullOrBlank() && !password.isNullOrBlank()
    }

    /*fun geUsersApp(code: String, password: String): Flow<RequestState<List<User>>> = flow {
        validateUser(code, password)
        emit(fetchUsers(code, password))
    }.catch { e ->
        emit(handleUserFetchError(e))
    }


    private fun validateUser(code: String, password: String) {
        if (!isUserValid(code, password)) {
            throw IllegalArgumentException("Usuario no válido")
        }
    }

    private suspend fun fetchUsers(code: String, password: String): RequestState<List<User>> {
        val users = userRepository.geUsersApp(code, password)
        return if (users.last().size > 0 && users.last().isNotEmpty()) {
            RequestState.Success(users.last())
        } else {
            RequestState.Error(Exception("Usuario no existe"))
        }
    }

    private fun handleUserFetchError(exception: Throwable): RequestState.Error {
        return RequestState.Error(Exception("Error al obtener usuarios", exception))
    }*/

}