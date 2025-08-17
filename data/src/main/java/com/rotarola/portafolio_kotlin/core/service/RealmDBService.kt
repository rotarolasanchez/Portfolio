package com.rotarola.portafolio_kotlin.core.service

import android.security.keystore.UserNotAuthenticatedException
import com.rotarola.portafolio_kotlin.data.BuildConfig
import com.rotarola.portafolio_kotlin.data.entity.UserApp
import com.rotarola.portafolio_kotlin.domain.model.RequestState
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class RealmDBService //@Inject constructor()
{

    private val app: App = App.Companion.create(BuildConfig.APP_ID)
    private lateinit var realm: Realm
    private var user = app.currentUser
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            user = app.login(Credentials.Companion.anonymous())
            val schema: Set<KClass<out RealmObject>> =
                setOf(UserApp::class)
            val config =
                SyncConfiguration.Builder(user!!, schema) // Replace with your partition value
                    .initialSubscriptions(rerunOnOpen = true) { sub ->
                        add(
                            query = sub.query<UserApp>()
                        )
                    }
                    .build()
            realm = Realm.Companion.open(config)
        }
    }

    fun getRealm(): Realm {
        return realm
    }

    suspend fun insertUserAPP(userApp: UserApp): RequestState<UserApp> {
        return if (user != null) {
            try {
                realm.write {
                    val addedDiary = copyToRealm(userApp.apply {
                        ownerID = user!!.id // Asegúrate de establecer el ownerId correctamente
                    })
                    RequestState.Success(data = addedDiary)
                }
            } catch (e: Exception) {
                RequestState.Error(e)
            }
        } else {
            RequestState.Error(
                UserNotAuthenticatedException()
            )
        }
    }
}