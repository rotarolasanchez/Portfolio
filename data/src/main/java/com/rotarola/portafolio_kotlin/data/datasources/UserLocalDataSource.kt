package com.rotarola.portafolio_kotlin.data.datasources

/*
import com.rotarola.portafolio_kotlin.core.service.RealmDBService
import com.rotarola.portafolio_kotlin.data.entity.UserApp
import io.realm.kotlin.ext.query

class UserLocalDataSource //@Inject constructor
    (
    private val realmDBService: RealmDBService
) {
    fun geUsersApp(code: String, password: String): List<UserApp> {
        var list: List<UserApp> = emptyList()
        try {
            list = realmDBService.getRealm().query<UserApp>()
                .query("code == $0 && password == $1", code, password)
                .find() // Execute the query
        } catch (e: Exception) {
            //Log.e("REOS", "UserDBRepository-geUsersApp: ${e.message}")
        }
        return list
    }
}*/