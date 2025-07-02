package eu.tutorials.gemverse.quizgame.data.model

data class QuizResponse(
    val response_code: Int,
    val results: List<Question>
)
