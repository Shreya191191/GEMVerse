package eu.tutorials.gemverse.games.quizgame.data.model

data class QuizResponse(
    val response_code: Int,
    val results: List<Question>
)
