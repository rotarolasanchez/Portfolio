package com.rotarola.portafolio_kotlin.domain.usecases

import com.rotarola.portafolio_kotlin.domain.model.RequestState
import com.rotarola.portafolio_kotlin.domain.model.User
import com.rotarola.portafolio_kotlin.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class LoginUseCase @Inject constructor
    (
    private val userRepository: UserRepository
) {
    fun geUsersApp(code: String, password: String): Flow<RequestState<List<User>>> {
        return flow {
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
}