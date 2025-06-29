package eu.tutorials.GEMVerse

import android.util.Log
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: ()-> Unit,

    onFacebookClick: () -> Unit = {},
    onXClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {}
){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
//
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Icon",
            modifier = Modifier.size(100.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Sign up to get started", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))

//


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

//        OutlinedTextField(
//            value = firstName,
//            onValueChange = { firstName = it },
//            label = { Text("First Name") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        )
//        OutlinedTextField(
//            value = lastName,
//            onValueChange = { lastName = it },
//            label = { Text("Last Name") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        )


        Button(
            onClick = {
                authViewModel.signUp(email, password
                    //, firstName, lastName
                )
                email = ""
                password = ""
//                firstName = ""
//                lastName = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {

//            IconButton(onClick = onFacebookClick) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_facebook),
//                    contentDescription = "Facebook",
//                    tint = Color.Unspecified,
//                    modifier = Modifier.size(32.dp)
//                )
//            }
//            IconButton(onClick = onXClick) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_x),
//                    contentDescription = "X",
//                    tint = Color.Unspecified,
//                    modifier = Modifier.size(32.dp)
//                )
//            }
            IconButton(onClick = {
                Log.d("ONE_TAP", "Google Sign-In button clicked")
                onGoogleClick()
            }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Already have an account? Sign in.",
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
    }
}
