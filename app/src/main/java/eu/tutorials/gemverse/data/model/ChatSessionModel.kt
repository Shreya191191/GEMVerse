package eu.tutorials.gemverse.data.model

import java.util.Date

data class ChatSessionModel(
    val sessionId: String = "",
    val title: String = "", // First question or custom title
    val timestamp: Date = Date()
)