package com.rotarola.data.repository

import android.util.Log
import com.rotarola.data.model.UserApp
import com.rotarola.data.util.database.RealmDBService
import io.realm.kotlin.ext.query
import javax.inject.Inject

class UserDBRepository @Inject constructor(
    private val realmDBService: RealmDBService
) {
    fun geUsersApp(code: String, password: String): List<UserApp> {
        Log.e("REOS", "UserDBRepository-geUsersApp.init")
        Log.e("REOS", "UserDBRepository-geUsersApp.code: $code")
        Log.e("REOS", "UserDBRepository-geUsersApp.password: $password")
        var list: List<UserApp> = emptyList()
        try {
            list = realmDBService.getRealm().query<UserApp>()
                .query("code == $0 && password == $1", code, password)
                .find() // Execute the query
        } catch (e: Exception) {
            Log.e("REOS", "UserDBRepository-geUsersApp: $e")
        }

        Log.e("REOS", "UserDBRepository-geUsersApp.list: ${list}")
        return list
    }
}

