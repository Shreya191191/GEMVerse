package eu.tutorials.gemverse.quizgame.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import eu.tutorials.gemverse.quizgame.QuizViewModel
import eu.tutorials.gemverse.ui.theme.Amber
import eu.tutorials.gemverse.ui.theme.Cyan


@Composable
fun QuizScreen(viewModel: QuizViewModel, onFinish: () -> Unit) {
    val questions = viewModel.questions.collectAsState().value
    val index = viewModel.currentIndex

    if (questions.isNotEmpty() && index < questions.size) {
        val question = questions[index]
        val correctAnswer = question.correct_answer
        val options = remember(question) {
            (question.incorrect_answers + correctAnswer).shuffled()
        }

        var selectedAnswer by remember { mutableStateOf<String?>(null) }
        var showAnswerResult by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF001F54))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Question:  ${index + 1} of ${questions.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = HtmlCompat.fromHtml(question.question, HtmlCompat.FROM_HTML_MODE_LEGACY).toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                options.forEach { option ->
                    val isCorrect = option == correctAnswer
                    val isSelected = option == selectedAnswer


                    val backgroundColor = when {
                        !showAnswerResult -> Color(0xFF0D47A1)
                        isCorrect -> Amber
                        isSelected -> Cyan
                        else -> Color(0xFF0D47A1)
                    }

                    Button(
                        onClick = {
                            if (!showAnswerResult) {
                                selectedAnswer = option
                                showAnswerResult = true
                                if (isCorrect) {
                                    viewModel.score++
                                }
                            }
                        },
                        enabled = !showAnswerResult,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = backgroundColor,
                            contentColor = Color.White,
                            disabledContainerColor = backgroundColor,
                            disabledContentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = option,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        if (showAnswerResult) {
                            when {
                                isCorrect -> Text(" ✔️  ", color = Color.White)
                                isSelected && !isCorrect -> Text(" ❌   ", color = Color.White)
                            }
                        }
                    }
                }

                if (showAnswerResult) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            selectedAnswer = null
                            showAnswerResult = false
                            if (index == questions.lastIndex) {
                                onFinish()
                            } else {
                                viewModel.currentIndex++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF001F54)
                        )
                    ) {
                        Text(if (index == questions.lastIndex) "Finish" else "Next")
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}




