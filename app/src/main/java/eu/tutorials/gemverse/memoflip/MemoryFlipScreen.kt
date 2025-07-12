package eu.tutorials.gemverse.memoflip

//package eu.tutorials.memoflip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryFlipScreen(viewModel: MemoryGameViewModel = viewModel()) {
    val cards by viewModel::cards

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "🧠 Memory Flip Game",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Move Count & High Score
        Text(
            text = "Moves: ${viewModel.moveCount}  |  High Score: ${
                if (viewModel.highScore == Int.MAX_VALUE) "—" else viewModel.highScore
            }",
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Cards (3x4)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 3 columns = 3x4 grid
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cards) { card ->
                Card(
                    onClick = { viewModel.onCardClicked(card) },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (card.isMatched) Color.LightGray else Color.White
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = if (card.isFaceUp || card.isMatched) card.emoji else "❓",
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result Message
        if (viewModel.gameWon) {
            Text(
                text = if (viewModel.moveCount == viewModel.highScore)
                    "🏆 New High Score!"
                else
                    "😔 Behind the High Scorer",
                fontSize = 22.sp,
                color = if (viewModel.moveCount == viewModel.highScore) Color(0xFF4CAF50) else Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }


        // Restart Button
        Button(
            onClick = { viewModel.resetGame() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Play Again")
        }
    }
}
