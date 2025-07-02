package eu.tutorials.gemverse.quizgame

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.tutorials.gemverse.quizgame.screens.QuizScreen
import eu.tutorials.gemverse.quizgame.screens.ResultScreen
import eu.tutorials.gemverse.quizgame.screens.WelcomeScreen



@Composable
fun QuizNavigation(
    viewModel: QuizViewModel,
    navToChat: () -> Unit
    ){

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            WelcomeScreen(
                onStartQuiz = {
                    navController.navigate("quiz")
                }
            )
        }
        composable("quiz") {
            QuizScreen(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate("result")
                }
            )
        }
        composable("result") {
            ResultScreen(
                score = viewModel.score,
                total = viewModel.total,
                onRetry = {
                    viewModel.reset()
                    navController.navigate("quiz") {
                        popUpTo("result") { inclusive = true }
                    }
                },
                onGoToChat = {
                    navToChat() // 👈 yeh navigate karega main nav controller se
                }
            )
        }
    }
}
