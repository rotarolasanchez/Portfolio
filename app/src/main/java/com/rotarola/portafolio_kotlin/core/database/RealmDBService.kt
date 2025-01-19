package com.rotarola.portafolio_kotlin.core.database

import android.security.keystore.UserNotAuthenticatedException
import com.rotarola.portafolio_kotlin.core.utils.Constans.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

class RealmDBService @Inject constructor() {
    private val app: App = App.create(APP_ID)
    private lateinit var realm: Realm
    private var user = app.currentUser
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            user = app.login(Credentials.anonymous())
            val schema: Set<KClass<out RealmObject>> =
                setOf(com.rotarola.portafolio_kotlin.data.model.UserApp::class)
            val config =
                SyncConfiguration.Builder(user!!, schema) // Replace with your partition value
                    .initialSubscriptions(rerunOnOpen = true) { sub ->
                        add(
                            query = sub.query<com.rotarola.portafolio_kotlin.data.model.UserApp>()
                        )
                    }
                    .build()
            realm = Realm.open(config)
        }
    }

    fun getRealm(): Realm {
        return realm
    }

    suspend fun insertUserAPP(userApp: com.rotarola.portafolio_kotlin.data.model.UserApp): com.rotarola.portafolio_kotlin.data.model.RequestState<com.rotarola.portafolio_kotlin.data.model.UserApp> {
        return if (user != null) {
            try {
                realm.write {
                    val addedDiary = copyToRealm(userApp.apply {
                        ownerID = user!!.id // Asegúrate de establecer el ownerId correctamente
                    })
                    com.rotarola.portafolio_kotlin.data.model.RequestState.Success(data = addedDiary)
                }
            } catch (e: Exception) {
                com.rotarola.portafolio_kotlin.data.model.RequestState.Error(e)
            }
        } else {
            com.rotarola.portafolio_kotlin.data.model.RequestState.Error(
                UserNotAuthenticatedException()
            )
        }
    }
}