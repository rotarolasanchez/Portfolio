package data.datasources

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthDataSource {
    val auth = FirebaseAuth.getInstance()
    suspend fun signInWithEmail(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        Log.e("REOS", "AuthDataSource-signInWithEmail-result.user: ${result.user}")
        return result.user
    }



}