package com.rotarola.portafolio_kotlin.data.repository

import android.util.Log
import com.rotarola.portafolio_kotlin.core.database.RealmDBService
import io.realm.kotlin.ext.query
import javax.inject.Inject

class UserDBRepository @Inject constructor(
    private val realmDBService: RealmDBService
) {
    fun geUsersApp(code: String, password: String): List<com.rotarola.portafolio_kotlin.data.model.UserApp> {
        var list: List<com.rotarola.portafolio_kotlin.data.model.UserApp> = emptyList()
        try {
            list = realmDBService.getRealm().query<com.rotarola.portafolio_kotlin.data.model.UserApp>()
                .query("code == $0 && password == $1", code, password)
                .find() // Execute the query
        } catch (e: Exception) {
            Log.e("REOS", "UserDBRepository-geUsersApp: ${e.message}")
        }
        return list
    }
}

