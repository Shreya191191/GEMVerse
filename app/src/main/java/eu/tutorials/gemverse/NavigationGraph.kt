package eu.tutorials.gemverse

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.tutorials.gemverse.quizgame.QuizNavigation
import eu.tutorials.gemverse.quizgame.QuizViewModel


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
                onSignUpSuccess = {
                    Log.d("SIGNUP", "NavigationGraph")
                    navController.navigate(Screen.ChatPage.route)
                },
                onGoogleClick = onGoogleSignIn,

            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                onSignInSuccess = {  navController.navigate(Screen.ChatPage.route)  },
                onGoogleClick = onGoogleSignIn
            )
        }

        composable(Screen.ChatPage.route){
            val chatViewModel = ChatViewModel() // 👈 ya ViewModelProvider se lelo agar chahiye
            ChatPage(
                viewModel = chatViewModel,
                onDrawerItemClick = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.QuizFlow.route) {
            val quizViewModel = viewModel<QuizViewModel>()
            QuizNavigation(
                viewModel = quizViewModel,
                navToChat = { navController.navigate(Screen.ChatPage.route)
            })
        }

    }
}