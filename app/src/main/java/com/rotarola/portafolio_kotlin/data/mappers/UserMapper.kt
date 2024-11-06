package com.rotarola.portafolio_kotlin.data.mappers

import com.rotarola.portafolio_kotlin.domain.entities.User
import com.rotarola.portafolio_kotlin.data.model.UserApp

fun  List<UserApp>.toUser(): List<User> {
    return map { establecimiento ->
        User(
            id = establecimiento.code,
            username = establecimiento.name,
            password = establecimiento.password,
        )
    }
}