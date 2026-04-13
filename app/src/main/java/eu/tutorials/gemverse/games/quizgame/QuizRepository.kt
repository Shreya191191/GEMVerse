package eu.tutorials.gemverse.games.quizgame


import eu.tutorials.gemverse.games.quizgame.data.model.Question
import eu.tutorials.gemverse.games.quizgame.data.network.RetrofitInstance

class QuizRepository {
    suspend fun fetchQuestions(): List<Question> {
        return RetrofitInstance.api.getQuestions().results

    }
}