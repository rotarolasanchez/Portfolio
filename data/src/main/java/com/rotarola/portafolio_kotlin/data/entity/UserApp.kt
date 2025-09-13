package com.rotarola.portafolio_kotlin.data.entity

import android.os.Build
import androidx.annotation.RequiresApi
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.Instant
import com.rotarola.portafolio_kotlin.core.utils.toRealmInstant

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
}