package com.example.feature_login.data.mappers

import com.example.feature_login.domain.entities.User
import com.rotarola.data.model.UserApp

fun  List<UserApp>.toUser(): List<User> {
    return map { establecimiento ->
        User(
            id = establecimiento.code,
            username = establecimiento.name,
            password = establecimiento.password,
        )
    }
}