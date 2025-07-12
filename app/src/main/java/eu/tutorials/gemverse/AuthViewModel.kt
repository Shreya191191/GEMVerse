package eu.tutorials.gemverse

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val userRepository: UserRepository

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> get() = _isLoggedIn

    init {
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
        _isLoggedIn.value = FirebaseAuth.getInstance().currentUser != null
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            Log.d("REPOT", "Repository")
            _isLoading.value = true
            val result = userRepository.signUp(email, password)
            _isLoading.value = false
            Log.d("REPOT", "Result from repository: $result")
            _authResult.value = result
            if (result is Result.Success && result.data == true) {
                _isLoggedIn.value = true
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            _authResult.value = result
            if (result is Result.Success && result.data == true) {
                _isLoggedIn.value = true
            }
        }
    }

    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            try {
                Log.d("GOOGLE_FLOW", "🔄 Signing in with Firebase using Google credential")
                FirebaseAuth.getInstance().signInWithCredential(credential).await()
                Log.d("GOOGLE_FLOW", "✅ Firebase sign-in successful")
                _authResult.value = Result.Success(true)
                _isLoggedIn.value = true
            } catch (e: Exception) {
                Log.e("GOOGLE_FLOW", "❌ Firebase sign-in failed: ${e.localizedMessage}")
                _authResult.value = Result.Error(e)
            }
        }
    }

    fun clearAuthResult() {
        _authResult.value = null
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        Log.d("LOGOUT_FLOW", "User signed out")
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Log.d("LOGOUT_FLOW", "Firebase User is NULL after signOut")
            _isLoggedIn.value = false
        } else {
            Log.d("LOGOUT_FLOW", "Firebase User STILL NOT null: ${user.email}")
            _isLoggedIn.value = false
        }
    }

}
