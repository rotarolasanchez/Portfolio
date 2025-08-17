package com.rotarola.portafolio_kotlin.data.mappers

import com.rotarola.portafolio_kotlin.data.entity.UserApp
import com.rotarola.portafolio_kotlin.domain.model.User


fun  List<UserApp>.toUser(): List<User> {
    return map { establecimiento ->
        User(
            id = establecimiento.code,
            username = establecimiento.name,
            password = establecimiento.password,
        )
    }
}