package eu.tutorials.gemverse.captain

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eu.tutorials.gemverse.R
import eu.tutorials.gemverse.Screen
import eu.tutorials.gemverse.ui.theme.MintGreen
import kotlin.random.Random
@Composable
fun CaptainGame(navController: NavController) {
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

    if (!gameOver) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏴‍☠️ Captain's Treasure Hunt 🗺️",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text("Treasure Found: $treasureFound", style = MaterialTheme.typography.titleMedium)
                Text("Current Direction: $direction", style = MaterialTheme.typography.titleMedium)
                Text("Result: $stormOrTreasure", style = MaterialTheme.typography.titleMedium)
                Text("Moves Left: $movesLeft", style = MaterialTheme.typography.titleMedium)

                listOf("East", "West", "North", "South").forEach { dir ->
                    Button(
                        onClick = {
                            direction = dir
                            if (Random.nextBoolean()) {
                                treasureFound++
                                stormOrTreasure = "🏆 We found a treasure!"
                            } else {
                                stormOrTreasure = "🌩️ Storm ahead!"
                            }
                            movesLeft--

                            if (movesLeft == 0) {
                                gameOver = true
                                result = if (treasureFound > 4) "🎉 You Win!" else "☠️ You Failed!"
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        enabled = !gameOver
                    ) {
                        Text("Sail $dir", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                    }
                }

                when (stormOrTreasure) {
                    "🏆 We found a treasure!" -> {
                        Image(
                            painter = painterResource(id = R.drawable.treasure2),
                            contentDescription = null,
                            modifier = Modifier.size(220.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    "🌩️ Storm ahead!" -> {
                        Image(
                            painter = painterResource(id = R.drawable.storm),
                            contentDescription = null,
                            modifier = Modifier.size(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    } else {
        // ✅ Game Over Screen
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MintGreen
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = result,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(
                        id = if (result.contains("Win")) R.drawable.treasure2 else R.drawable.storm
                    ),
                    contentDescription = "Result Image",
                    modifier = Modifier.size(300.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { resetGame() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("🔁 Restart Game", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        navController.navigate(Screen.ChatPage.route)
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("💬 Go To My BOT", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                }
            }
        }
    }
}
