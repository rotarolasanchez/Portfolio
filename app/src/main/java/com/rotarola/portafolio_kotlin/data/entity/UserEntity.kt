package com.rotarola.portafolio_kotlin.data.entity

/*
//@RealmClass(embedded = true)
open class UserApp : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var code: String = ""
    var name: String = ""
    var password: String = ""
    var ownerID: String = ""
    @RequiresApi(Build.VERSION_CODES.O)
    var date: RealmInstant = Instant.now().toRealmInstant()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun toString(): String {
        return "UserApp(_id=$_id, code='$code', name='$name', password='$password', ownerID='$ownerID', date=$date)"
    }
}*/

data class User(
    val id: String = "",           // UID de Firebase Auth
    val email: String = "",
    val name: String = "",
    val photoUrl: String = "",
    val role: String = "user",
    val createdAt: Long = 0L,
    val lastLoginAt: Long = 0L,
    val password: String = "",
    val user: String = ""
)