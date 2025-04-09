package edu.ntnu.idatt2105.gr2.backend.dto

import java.sql.Timestamp

data class ConversationCardResponse(
    val id: Int,
    val itemTitle: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val containsUnreadMessages: Boolean
)

data class ConversationsCardsResponse(
    val conversations: List<ConversationCardResponse>
)

data class ConversationCardsRequest (
    val userId: Int
)

data class NewMessageResponse(
    val conversationId: Int,
    val senderId: Int,
    val content: String,
    val sentAt: String
)

data class Messages(
    val id: Int,
    val conversationId: Int,
    val senderId: Int,
    val content: String,
    val sentAt: String,
    val isRead: Boolean
)

data class CreateConversationResponse(
    val statusMessage : String
)

data class CreateConversationRequest(
    val itemId: Int,
)