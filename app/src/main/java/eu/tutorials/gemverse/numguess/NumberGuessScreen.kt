package eu.tutorials.gemverse.numguess

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NumberGuessScreen(viewModel: NumberGuessViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎯 Number Guessing Game", fontSize = 26.sp)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.userInput,
            onValueChange = { viewModel.userInput = it },
            label = { Text("Enter a number (1–100)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            enabled = !viewModel.isGameOver,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.onGuess() },
            enabled = !viewModel.isGameOver
        ) {
            Text("Guess")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(viewModel.feedback, fontSize = 18.sp)

        if (viewModel.isGameOver) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.resetGame() }) {
                Text("Play Again")
            }
        }
    }
}