package com.rotarola.portafolio_kotlin.domain.repositories

import com.rotarola.portafolio_kotlin.domain.model.User
import kotlinx.coroutines.flow.Flow


interface UserRepository
{
    fun geUsersApp(code: String, password: String): Flow<List<User>>
}