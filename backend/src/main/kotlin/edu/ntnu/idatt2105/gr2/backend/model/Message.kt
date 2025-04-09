package edu.ntnu.idatt2105.gr2.backend.model

import java.sql.Timestamp

class Message (
    val id: Int = -1,
    val conversationId: Int = -1,
    val senderId: Int = -1,
    var content: String = "",
    var sentAt: Timestamp,
    var isRead: Boolean = false,
) {
    init {
        require(id >= 0) { "ID must be non-negative" }
        require(conversationId >= 0) { "Conversation ID must be non-negative" }
        require(senderId >= 0) { "Sender ID must be non-negative" }
        require(content.isNotBlank()) { "Content cannot be blank" }
        require(sentAt.time > 0) { "Sent time must be a valid timestamp" }
        require(!isRead) { "isRead must be false upon initialization" }
    }
}