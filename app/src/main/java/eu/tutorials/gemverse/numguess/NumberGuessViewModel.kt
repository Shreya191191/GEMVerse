package eu.tutorials.gemverse.numguess

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class NumberGuessViewModel : ViewModel() {
    private var targetNumber by mutableStateOf(generateRandomNumber())
    var userInput by mutableStateOf("")
    var feedback by mutableStateOf("")
    var attempts by mutableStateOf(0)
    var isGameOver by mutableStateOf(false)

    private fun generateRandomNumber(): Int {
        return Random.nextInt(1, 101) // Number between 1 and 100
    }

    fun onGuess() {
        val guess = userInput.toIntOrNull()
        if (guess == null || guess !in 1..100) {
            feedback = "❗ Enter a number between 1 and 100"
            return
        }

        attempts++

        when {
            guess < targetNumber -> feedback = "🔽 Too Low"
            guess > targetNumber -> feedback = "🔼 Too High"
            else -> {
                feedback = "✅ Correct! You guessed in $attempts attempts"
                isGameOver = true
            }
        }
    }

    fun resetGame() {
        targetNumber = generateRandomNumber()
        userInput = ""
        feedback = ""
        attempts = 0
        isGameOver = false
    }
}
