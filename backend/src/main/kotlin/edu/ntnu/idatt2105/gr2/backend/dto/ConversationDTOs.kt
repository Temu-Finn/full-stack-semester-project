package edu.ntnu.idatt2105.gr2.backend.dto

import java.time.Instant

/**
 * Represents a conversation card with basic information about the conversation
 */
data class ConversationCardResponse(
    val id: Int,
    val lastMessage: String?,
    val lastMessageTime: Instant?,
    val item: ItemCard,
)

data class ConversationsResponse(
    val conversations: List<ConversationCardResponse>,
)

data class NewMessageResponse(
    val conversationId: Int,
    val senderId: Int,
    val content: String,
    val sentAt: String,
)

data class MessageResponse(
    val id: Int,
    val conversationId: Int,
    val senderId: Int,
    val content: String,
    val sentAt: Instant,
    val isRead: Boolean,
)

data class SendMessageRequest(
    val conversationId: Int? = null,
    val itemId: Int,
    val content: String,
)

data class GetConversationResponse(
    val otherParticipantName: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val messages: List<MessageResponse>,
    val item: ItemCard,
)
