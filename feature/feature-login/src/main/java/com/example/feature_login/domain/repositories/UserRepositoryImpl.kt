package com.example.feature_login.domain.repositories

import android.util.Log
import com.example.feature_login.data.mappers.toUser
import com.example.feature_login.domain.entities.User
import com.rotarola.data.model.UserApp
import com.rotarola.data.repository.UserDBRepository
import com.rotarola.data.util.database.RealmDBService
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val datasource: UserDBRepository) : UserRepository {

    override fun geUsersApp(code: String,password: String): Flow<List<User>> = flow {
        Log.e("REOS", "UserRepositoryImpl-geUsersApp.init")
        val response = datasource.geUsersApp(code, password)
        Log.e("REOS", "UserRepositoryImpl-geUsersApp.response: $response")
        emit(response.toUser() ?: listOf())
    }

}