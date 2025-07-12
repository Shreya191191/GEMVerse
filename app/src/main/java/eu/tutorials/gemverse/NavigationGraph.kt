package eu.tutorials.gemverse

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import eu.tutorials.gemverse.captain.CaptainGame
import eu.tutorials.gemverse.memoflip.MemoryFlipScreen
import eu.tutorials.gemverse.memoflip.MemoryGameViewModel
import eu.tutorials.gemverse.numguess.NumberGuessScreen
import eu.tutorials.gemverse.numguess.NumberGuessViewModel
import eu.tutorials.gemverse.quizgame.QuizNavigation
import eu.tutorials.gemverse.quizgame.QuizViewModel
import eu.tutorials.gemverse.tictactoe.TicTacToeScreen


@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onGoogleSignIn: () -> Unit
) {
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState()

    val startDestination = if (isLoggedIn == true) {
        Screen.ChatPage.route
    } else {
        Screen.SignupScreen.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.SignupScreen.route) {
            SignUpScreen(
                authViewModel=authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route){
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    Log.d("SIGNUP", "NavigationGraph")
                    navController.navigate(Screen.ChatPage.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onGoogleClick = onGoogleSignIn,

            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignupScreen.route){
                        popUpTo(0) { inclusive = true }
                    }
                 },
                onSignInSuccess = {
                    navController.navigate(Screen.ChatPage.route) {
                      popUpTo(0) { inclusive = true }
                   }
               },
                onGoogleClick = onGoogleSignIn
            )
        }

        composable(Screen.ChatPage.route) {
            val chatViewModel = ChatViewModel()
            ChatPage(
                viewModel = chatViewModel,
                onDrawerItemClick = { route ->
                    if (route == Screen.LogOut.route) {
                        authViewModel.logout()
                        Log.d("LOGOUT_FLOW", "Logout clicked")
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

        composable(Screen.TicTacToe.route) {
            TicTacToeScreen(navController)
        }

        composable(Screen.CaptainGame.route) {
           CaptainGame(navController)
        }

        composable(Screen.NumberGuess.route) {
            val numberGuessViewModel = viewModel<NumberGuessViewModel>()
            NumberGuessScreen(
                navController = navController,
                viewModel = numberGuessViewModel
            )
        }

        composable(Screen.MemoFlip.route) {
            val memoryGameViewModel = viewModel<MemoryGameViewModel>()
            MemoryFlipScreen(
                navController = navController,
                viewModel = memoryGameViewModel
            )
        }

    }
}