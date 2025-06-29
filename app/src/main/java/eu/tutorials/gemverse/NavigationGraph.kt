package eu.tutorials.GEMVerse

import androidx.compose.runtime.Composable

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onGoogleSignIn: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignupScreen.route
    ) {
        composable(Screen.SignupScreen.route) {
            SignUpScreen(
                authViewModel=authViewModel,
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route)
                },
                //
                onGoogleClick = onGoogleSignIn,

                //
            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                onSignInSuccess = { /* TODO: Navigate to home screen if needed */ },
                onGoogleClick = onGoogleSignIn
            )
        }
    }
}