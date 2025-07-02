package eu.tutorials.gemverse

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import eu.tutorials.gemverse.ui.theme.GEMVerseTheme


class MainActivity : ComponentActivity() {

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient
    private var googleSignInCallback: ((AuthCredential) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Clear last signed-in account to avoid auto sync behavior
        googleSignInClient.signOut()

        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.d("GOOGLE_FLOW", "👉 Received result from Google Sign-In intent")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("GOOGLE_FLOW", "✅ Google account received: ${account.email}")
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                googleSignInCallback?.invoke(credential)
            } catch (e: ApiException) {
                Log.e("GOOGLE_FLOW", "❌ Google Sign-In failed: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }

        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val navController = rememberNavController()

            googleSignInCallback = { credential ->
                Log.d("GOOGLE_FLOW", "👉 Passing credential to ViewModel")
                authViewModel.signInWithGoogle(credential)
            }

            GEMVerseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        onGoogleSignIn = {
                            Log.d("GOOGLE_FLOW", "👉 Launching Google Sign-In intent")
                            // This ensures account picker appears every time
                            googleSignInClient.signOut().addOnCompleteListener {
                                val signInIntent = googleSignInClient.signInIntent
                                Log.d("GOOGLE_FLOW", "✅ Sign-out complete, launching intent")
                                googleSignInLauncher.launch(signInIntent)
                            }
                        }
                    )
                }
            }
        }
    }
}


