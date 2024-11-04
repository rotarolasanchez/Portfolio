package com.example.feature_login.domain.repositories

import com.example.feature_login.domain.entities.User
import kotlinx.coroutines.flow.Flow


interface UserRepository
{
    fun geUsersApp(code: String, password: String): Flow<List<User>>
}