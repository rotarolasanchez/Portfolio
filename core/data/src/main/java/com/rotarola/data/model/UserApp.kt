package com.rotarola.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.rotarola.data.util.database.toRealmInstant
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.Instant

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