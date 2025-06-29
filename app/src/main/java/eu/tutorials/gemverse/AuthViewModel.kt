package eu.tutorials.GEMVerse

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

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    fun signUp(email: String, password: String
        //, firstName: String, lastName: String
    ) {
        viewModelScope.launch {
            _authResult.value = userRepository.signUp(email, password
                //, firstName, lastName
            )
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
                FirebaseAuth.getInstance().signInWithCredential(credential).await()
                _authResult.value = Result.Success(true)
            } catch (e: Exception) {
                _authResult.value = Result.Error(e)
            }
        }
    }
}