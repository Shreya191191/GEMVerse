package eu.tutorials.gemverse.memoflip

//package eu.tutorials.memoflip

data class MemoryCard(
    val id: Int,
    val emoji: String,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)
