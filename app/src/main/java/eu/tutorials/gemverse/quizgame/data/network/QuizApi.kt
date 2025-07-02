package eu.tutorials.gemverse.quizgame.data.network

import eu.tutorials.gemverse.quizgame.data.model.QuizResponse
import retrofit2.http.GET

interface QuizApi {
    @GET("api.php?amount=10&type=multiple") // your real endpoint
    suspend fun getQuestions(): QuizResponse

}