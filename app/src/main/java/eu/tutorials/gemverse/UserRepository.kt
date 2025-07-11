package eu.tutorials.gemverse
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class UserRepository(private val auth: FirebaseAuth,
                     private val firestore: FirebaseFirestore
){
    suspend fun signUp(email: String, password: String
    ): Result<Boolean> =
        try {
            Log.d("REPOT", "Starting")
            auth.createUserWithEmailAndPassword(email, password).await()
            val user= User(
                email)
            saveUserToFirestore(user)
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }

    private suspend fun saveUserToFirestore(user: User) {
        try {
            Log.d("REPOT", "Saving user to Firestore:${user.email}")
            withTimeout(5000L) { // 5 second timeout
                firestore.collection("users")
                    .document(user.email)
                    .set(user)
                    .await()
            }
        } catch (e: Exception) {
            Log.e("REPOT", "Firestore exception: ${e.message}", e)
        }
    }

    suspend fun login(email: String, password: String): Result<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }


    fun logout(): Result<Boolean> {
        return try {
            auth.signOut()
            Log.d("LogOut_Flow","LogOut")
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


}

