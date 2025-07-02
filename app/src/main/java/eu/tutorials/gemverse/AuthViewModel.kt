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

    init {
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
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
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }

    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            try {
                Log.d("GOOGLE_FLOW", "🔄 Signing in with Firebase using Google credential")
                FirebaseAuth.getInstance().signInWithCredential(credential).await()
                Log.d("GOOGLE_FLOW", "✅ Firebase sign-in successful")
                _authResult.value = Result.Success(true)
            } catch (e: Exception) {
                Log.e("GOOGLE_FLOW", "❌ Firebase sign-in failed: ${e.localizedMessage}")
                _authResult.value = Result.Error(e)
            }
        }
    }

    fun clearAuthResult() {
        _authResult.value = null
    }

}
