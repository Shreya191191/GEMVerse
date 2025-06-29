package eu.tutorials.GEMVerse


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.GEMVerse.AuthViewModel
import eu.tutorials.GEMVerse.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    onSignInSuccess: () -> Unit,
    //
//    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
//    onSignUpClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {},
    onXClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {}
    //
) {
    var email by remember { mutableStateOf("") }
    var password by remember {
        mutableStateOf("")
    }

    val result by authViewModel.authResult.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            modifier = Modifier.size(100.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title & Subtitle
        Text(text = "Welcome Back", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Login to your account", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                authViewModel.login(email, password)
                when (result) {
                    is Result.Success->{
                        onSignInSuccess()
                    }
                    is Result.Error ->{

                    }

                    else -> {

                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Login")
        }

        //
        Spacer(modifier = Modifier.height(12.dp))
        // Forgot Password
        Text(
            text = "Forgot Password?",
            color = Color.Blue,
            modifier = Modifier.clickable { onForgotPasswordClick() }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Divider text
        Text(text = "OR sign in with", color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(12.dp))

        // Social Icons Row
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
//            IconButton(onClick = onFacebookClick) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_facebook),
//                    contentDescription = "Facebook",
//                    modifier = Modifier.size(32.dp),
//                    tint = Color.Unspecified
//                )
//            }
//            IconButton(onClick = onXClick) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_x),
//                    contentDescription = "X",
//                    modifier = Modifier.size(32.dp),
//                    tint = Color.Unspecified
//                )
//            }
            IconButton(onClick = onGoogleClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
            }
        }

        // Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))
        Text("Don't have an account? Sign up.",
            modifier = Modifier.clickable {  onNavigateToSignUp() }
        )
    }
}

