package eu.tutorials.gemverse.memoflip

//package eu.tutorials.memoflip

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MemoryGameViewModel : ViewModel() {

    private val emojis = listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊")
    private var selectedCards = mutableListOf<MemoryCard>()

    var cards by mutableStateOf(shuffleCards())
        private set

    var moveCount by mutableStateOf(0)
        private set

    var highScore by mutableStateOf(Int.MAX_VALUE)
        private set

    var gameOver by mutableStateOf(false)
        private set

    var gameWon by mutableStateOf(false)
        private set

    private fun shuffleCards(): List<MemoryCard> {
        val allEmojis = (emojis + emojis).shuffled()
        return allEmojis.mapIndexed { index, emoji ->
            MemoryCard(id = index, emoji = emoji)
        }
    }

    fun onCardClicked(card: MemoryCard) {
        if (card.isFaceUp || card.isMatched || selectedCards.size == 2 || gameOver || gameWon) return

        val updated = cards.map {
            if (it.id == card.id) it.copy(isFaceUp = true) else it
        }
        cards = updated

        selectedCards.add(updated.first { it.id == card.id })

        if (selectedCards.size == 2) {
            moveCount++
            checkForMatch()
        }
    }

    private fun checkForMatch() {
        viewModelScope.launch {
            delay(1000)
            val (first, second) = selectedCards
            if (first.emoji == second.emoji) {
                cards = cards.map {
                    if (it.id == first.id || it.id == second.id)
                        it.copy(isMatched = true)
                    else it
                }
                if (cards.all { it.isMatched || it.id == first.id || it.id == second.id }) {
                    gameWon = true
                    if (moveCount < highScore) {
                        highScore = moveCount
                    }
                }
            } else {
                cards = cards.map {
                    if (it.id == first.id || it.id == second.id)
                        it.copy(isFaceUp = false)
                    else it
                }
            }
            selectedCards.clear()
        }
    }

    fun resetGame() {
        cards = shuffleCards()
        moveCount = 0
        gameOver = false
        gameWon = false
        selectedCards.clear()
    }
}
