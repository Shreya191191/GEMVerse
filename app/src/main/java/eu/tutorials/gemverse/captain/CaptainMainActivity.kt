package eu.tutorials.gemverse.captain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import eu.tutorials.gemverse.ui.theme.GEMVerseTheme
import kotlin.random.Random
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import eu.tutorials.GEMVerse.R


class CaptainMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GEMVerseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CaptainGame()
                }
            }
        }
    }

    @Composable
    fun CaptainGame() {
        var treasureFound by remember { mutableStateOf(0) }
        var direction by remember { mutableStateOf("North") }
        var stormOrTreasure by remember { mutableStateOf("") }
        var movesLeft by remember { mutableStateOf(10) }
        var gameOver by remember { mutableStateOf(false) }
        var result by remember { mutableStateOf("") }

        fun resetGame() {
            treasureFound = 0
            direction = "North"
            stormOrTreasure = ""
            movesLeft = 10
            gameOver = false
            result = ""
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏴‍☠️ Captain's Treasure Hunt 🗺️",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(text = "Treasure Found: $treasureFound")
                Text(text = "Current Direction: $direction")
                Text(text = "Result: $stormOrTreasure")
                Text(text = "Moves Left: $movesLeft")

                // Direction buttons
                listOf("East", "West", "North", "South").forEach { dir ->
                    Button(
                        onClick = {
                            if (!gameOver) {
                                direction = dir
                                if (Random.nextBoolean()) {
                                    treasureFound += 1
                                    stormOrTreasure = "🏆 We found a treasure!"
                                } else {
                                    stormOrTreasure = "🌩️ Storm ahead!"
                                }
                                movesLeft -= 1

                                if (movesLeft == 0) {
                                    gameOver = true
                                    result = if (treasureFound > 4)
                                        "🎉 You Win!" else "☠️ You Failed!"
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        enabled = !gameOver
                    ) {
                        Text("Sail $dir")
                    }
                }

                // 🖼️ Show image after each move
                when (stormOrTreasure) {
                    "🏆 We found a treasure!" -> {
                        Image(
                            painter = painterResource(id = R.drawable.treasure2),
                            contentDescription = "Treasure Found",
                            modifier = Modifier.size(200.dp),
                            //contentScale = ContentScale.Fit
                            contentScale = ContentScale.Crop  // ✅ This removes empty spacing visually
                        )
                    }
                    "🌩️ Storm ahead!" -> {
                        Image(
                            painter = painterResource(id = R.drawable.storm),
                            contentDescription = "Storm Ahead",
                            modifier = Modifier.size(200.dp),
                            //   contentScale = ContentScale.Fit
                            contentScale = ContentScale.Crop  // ✅ This removes empty spacing visually
                        )
                    }
                }

                // Game over result and restart button
                if (gameOver) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = result,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { resetGame() },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🔁 Restart Game")
                    }
                }
            }
        }
    }

}