package eu.tutorials.gemverse.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.gemverse.ui.theme.GEMVerseTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                GEMVerseTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var gameMode by remember { mutableStateOf<String?>(null) }

                    if (gameMode == null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Choose Game Mode", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = { gameMode = "Human" }) {
                                Text("Human vs Human")
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { gameMode = "AI" }) {
                                Text("Human vs AI")
                            }
                        }
                    } else {
                        TicTacToeGame(gameMode!!) {
                            gameMode = null // reset game
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun TicTacToeGame(gameMode: String, onRestartGame: () -> Unit) {
    val board = remember { List(3) { List(3) { mutableStateOf("") } } }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var winningCells by remember { mutableStateOf<List<Pair<Int, Int>>?>(null) }

    var xWins by remember { mutableStateOf(0) }
    var oWins by remember { mutableStateOf(0) }
    var draws by remember { mutableStateOf(0) }

    var aiPending by remember { mutableStateOf(false) }

    fun checkWinner(): String? {
        val lines = listOf(
            // rows
            listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2)),
            listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2)),
            listOf(Pair(2, 0), Pair(2, 1), Pair(2, 2)),
            // cols
            listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0)),
            listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1)),
            listOf(Pair(0, 2), Pair(1, 2), Pair(2, 2)),
            // diagonals
            listOf(Pair(0, 0), Pair(1, 1), Pair(2, 2)),
            listOf(Pair(0, 2), Pair(1, 1), Pair(2, 0))
        )
        for (line in lines) {
            val (a, b, c) = line
            val valueA = board[a.first][a.second].value
            val valueB = board[b.first][b.second].value
            val valueC = board[c.first][c.second].value
            if (valueA == valueB && valueB == valueC && valueA != "") {
                winningCells = line
                return valueA
            }
        }
        if (board.all { row -> row.all { it.value != "" } }) {
            return "Draw"
        }
        return null
    }

    fun aiMove() {
        val empty = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].value == "") {
                    empty.add(Pair(i, j))
                }
            }
        }
        if (empty.isNotEmpty()) {
            val (i, j) = empty[Random.nextInt(empty.size)]
            board[i][j].value = "O"
            winner = checkWinner()

            when (winner) {
                "X" -> xWins++
                "O" -> oWins++
                "Draw" -> draws++
            }

            if (winner == null) currentPlayer = "X"
        }
    }

    LaunchedEffect(aiPending) {
        if (aiPending && gameMode == "AI" && winner == null) {
            delay(400)
            aiMove()
            aiPending = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tic Tac Toe - ${if (gameMode == "AI") "vs AI" else "vs Human"}", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(12.dp))

        // Scoreboard
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.padding(bottom = 12.dp)) {
            Text("❌ X: $xWins", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text("⭕ O: $oWins", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text("🤝 Draws: $draws", fontSize = 16.sp)
        }

        // Game Grid
        for (i in 0..2) {
            Row {
                for (j in 0..2) {
                    val isWinningCell = winningCells?.contains(Pair(i, j)) == true
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .border(2.dp, Color.Black)
                            .background(
                                if (isWinningCell) Color.Red.copy(alpha = 0.3f)
                                else Color.White
                            )
                            .clickable(enabled = board[i][j].value == "" && winner == null) {
                                board[i][j].value = currentPlayer
                                winner = checkWinner()

                                when (winner) {
                                    "X" -> xWins++
                                    "O" -> oWins++
                                    "Draw" -> draws++
                                }

                                if (gameMode == "AI" && winner == null) {
                                    currentPlayer = "O"
                                    aiPending = true
                                } else if (winner == null) {
                                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(board[i][j].value, fontSize = 36.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        winner?.let {
            Text(
                text = if (it == "Draw") "It's a Draw!" else "Winner: $it",
                fontSize = 20.sp,
                color = if (it == "Draw") Color.Gray else Color.Green
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                // Only clear board
                for (i in 0..2) {
                    for (j in 0..2) {
                        board[i][j].value = ""
                    }
                }
                winner = null
                winningCells = null
                currentPlayer = "X"
                aiPending = false
            }) {
                Text("Restart Game")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                // Reset everything: board + scores
                for (i in 0..2) {
                    for (j in 0..2) {
                        board[i][j].value = ""
                    }
                }
                winner = null
                winningCells = null
                currentPlayer = "X"
                xWins = 0
                oWins = 0
                draws = 0
                aiPending = false
                onRestartGame()
            }) {
                Text("Back to Mode Selection")
            }
        }
    }
}
