package com.rotarola.portafolio_kotlin.core

/*
@RunWith(JUnit4::class)
class RealmDBServiceTest {

    private val app: App = App.create(APP_ID)
    private lateinit var realm: Realm
    private var user = app.currentUser
    private val scope = CoroutineScope(Dispatchers.IO)

    @Before
    fun setUp() {
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

    @Test
    fun `test connection and authentication`() = runBlocking {
        val user = app.login(Credentials.anonymous())
        assertNotNull("User should be authenticated", user)

        val schema: Set<KClass<out RealmObject>> = setOf(UserApp::class)
        val config = SyncConfiguration.Builder(user, schema).build()
        val realm = Realm.open(config)
        assertNotNull("Realm should be opened", realm)
        realm.close() // Cierra el Realm después de usarlo para evitar problemas
    }
}

 */
