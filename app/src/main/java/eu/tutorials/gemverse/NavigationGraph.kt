package eu.tutorials.gemverse

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import eu.tutorials.gemverse.captain.CaptainGame
import eu.tutorials.gemverse.quizgame.QuizNavigation
import eu.tutorials.gemverse.quizgame.QuizViewModel
import eu.tutorials.gemverse.tictactoe.TicTacToeScreen


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

        composable(Screen.ChatPage.route) {
            val chatViewModel = ChatViewModel()
            ChatPage(
                viewModel = chatViewModel,
                onDrawerItemClick = { route ->
                    if (route == "logout") {
                        // Pehle logout karo
                        FirebaseAuth.getInstance().signOut()
                        // Sab backstack hata ke LoginScreen pe jao
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(route)
                    }
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
//////////////
        composable(Screen.TicTacToe.route) {
            TicTacToeScreen(navController)
        }

        composable(Screen.CaptainGame.route) {
           CaptainGame(navController)
        }

    }
}