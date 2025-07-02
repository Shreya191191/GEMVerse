package eu.tutorials.gemverse

sealed class Screen(val route:String){
    object LoginScreen: Screen("loginscreen")
    object SignupScreen: Screen("signupscreen")
    object ChatPage : Screen("chatpage")

    //This for quiz navigation:
    object QuizFlow : Screen("quiz_flow")
}