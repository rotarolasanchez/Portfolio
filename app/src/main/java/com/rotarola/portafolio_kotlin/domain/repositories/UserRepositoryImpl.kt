package com.rotarola.portafolio_kotlin.domain.repositories

import android.util.Log
import com.rotarola.portafolio_kotlin.data.mappers.toUser
import com.rotarola.portafolio_kotlin.data.repository.UserDBRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val datasource: UserDBRepository
) : UserRepository {

    override fun geUsersApp(code: String,password: String): Flow<List<com.rotarola.portafolio_kotlin.domain.entities.User>> = flow {
        Log.e("REOS", "UserRepositoryImpl-geUsersApp.init")
        val response = datasource.geUsersApp(code, password)
        Log.e("REOS", "UserRepositoryImpl-geUsersApp.response: $response")
        emit(response.toUser() ?: listOf())
    }

}