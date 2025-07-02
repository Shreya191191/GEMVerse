package eu.tutorials.gemverse.quizgame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.tutorials.gemverse.quizgame.data.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {
    private val repository = QuizRepository()

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    var currentIndex by mutableStateOf(0)
    var score = 0

    init {
        loadQuestions() // ✅ Load when ViewModel is created
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _questions.value = repository.fetchQuestions()
        }
    }
    val total: Int
        get() = _questions.value.size
    fun nextQuestion(isCorrect: Boolean) {
        if (isCorrect) score++
        currentIndex++
    }

    fun reset() {
        currentIndex = 0
        score = 0
        loadQuestions()
    }
}