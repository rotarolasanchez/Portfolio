package com.rotarola.portafolio_kotlin.core.database

import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
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
        //withContext(Dispatchers.IO) {
            Log.e("REOS", "RealmDBService -user?.loggedIn: " + user?.loggedIn)
            //if (user?.loggedIn == false) {
                Log.e("REOS", "RealmDBService -app" + app)
                Log.e("REOS", "RealmDBService -app" )
                scope.launch {
                user = app.login(Credentials.anonymous())
                Log.e("REOS", "RealmDBService -user" + user)
                val schema: Set<KClass<out RealmObject>> = setOf(com.rotarola.portafolio_kotlin.data.model.UserApp::class)
                Log.e("REOS", "RealmDBService -schema" + schema)
                val config =
                    SyncConfiguration.Builder(user!!, schema) // Replace with your partition value
                        .initialSubscriptions(rerunOnOpen = true) { sub ->
                            add(
                                query = sub.query<com.rotarola.portafolio_kotlin.data.model.UserApp>()
                            )
                        }
                        .build()
                realm = Realm.open(config)
                Log.e("REOS", "RealmDBService -realm" + realm)
           // }
        }
    }

    fun getRealm(): Realm {
        return realm
    }

    suspend fun insertUserAPP(userApp: com.rotarola.portafolio_kotlin.data.model.UserApp): com.rotarola.portafolio_kotlin.data.model.RequestState<com.rotarola.portafolio_kotlin.data.model.UserApp> {
        Log.e("REOS", "RealmDBService-insertUserAPP.init")
        return if (user != null ) {
            try {
                //realm.subscriptions.waitForSynchronization()  // Asegura que se hayan sincronizado los datos
                realm.write {
                    Log.e("REOS", "RealmDBService-insertUserAPP.diary: ${userApp}")
                    //val addedDiary = copyToRealm(diary)
                    val addedDiary = copyToRealm(userApp.apply {
                        ownerID = user!!.id // Asegúrate de establecer el ownerId correctamente
                    })
                    Log.e("REOS", "RealmDBService-insertUserAPP.addedDiary: ${addedDiary}")
                    com.rotarola.portafolio_kotlin.data.model.RequestState.Success(data = addedDiary)
                }
            } catch (e: Exception) {
                Log.e("REOS", "RealmDBService-insertUserAPP.exception: ${e.message}")
                com.rotarola.portafolio_kotlin.data.model.RequestState.Error(e)
            }
        } else {
            Log.e("REOS", "RealmDBService-insertUserAPP.Error: User is not authenticated or realm is invalid.")
            com.rotarola.portafolio_kotlin.data.model.RequestState.Error(UserNotAuthenticatedException())
        }
    }

    /*fun getAllUsersApp(): List<UserApp> {
        val list = realm.query<UserApp>().find()
        Log.e("REOS", "RealmDBService-getAllUsersApp.list: ${list}")
        return list
    }*/
}