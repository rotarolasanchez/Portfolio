package com.rotarola.portafolio_kotlin.data.repository

import com.rotarola.portafolio_kotlin.data.datasources.UserLocalDataSource

import com.rotarola.portafolio_kotlin.domain.model.User
import com.rotarola.portafolio_kotlin.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.rotarola.portafolio_kotlin.data.mappers.toUser


class UserRepositoryImpl @Inject constructor
    (
    private val datasource: UserLocalDataSource
) : UserRepository {

    override fun geUsersApp(code: String,password: String): Flow<List<User>> = flow {
        //Log.e("REOS", "UserRepositoryImpl-geUsersApp.init")
        val response = datasource.geUsersApp(code, password)
        //Log.e("REOS", "UserRepositoryImpl-geUsersApp.response: $response")
        emit(response.toUser() ?: listOf())
    }

}