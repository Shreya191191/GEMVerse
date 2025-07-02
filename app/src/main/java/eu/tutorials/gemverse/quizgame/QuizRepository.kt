package eu.tutorials.gemverse.quizgame


import eu.tutorials.gemverse.quizgame.data.model.Question
import eu.tutorials.gemverse.quizgame.data.network.RetrofitInstance

class QuizRepository {
    suspend fun fetchQuestions(): List<Question> {
        return RetrofitInstance.api.getQuestions().results

    }
}