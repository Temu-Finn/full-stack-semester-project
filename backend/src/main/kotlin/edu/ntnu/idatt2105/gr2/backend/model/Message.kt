package edu.ntnu.idatt2105.gr2.backend.model

import java.time.LocalDateTime

data class Message (
    val id: Int = -1,
    val conversationId: Int = -1,
    val senderId: Int = -1,
    var content: String = "",
    var sentAt: LocalDateTime,
    var isRead: Boolean = false,
) {
    init {
        require(id == -1 || id >= 0) { "ID must be -1 for unsaved messages or non-negative for saved messages" }
        require(conversationId >= 0) { "Conversation ID must be non-negative" }
        require(senderId >= 0) { "Sender ID must be non-negative" }
        require(content.isNotBlank()) { "Content cannot be blank" }
    }
}