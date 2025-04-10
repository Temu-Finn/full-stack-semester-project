package edu.ntnu.idatt2105.gr2.backend.dto

import java.time.Instant

/**
 * Represents a conversation card with basic information about the conversation
 */
data class ConversationCardResponse(
    val id: Int,
    val lastMessage: String?,
    val lastMessageTime: Instant?,
    val item: ItemCard
)

/**
 * Represents a list of conversations with their basic information
 */

data class ConversationsResponse(
    val conversations: List<ConversationCardResponse>
)

/**
 * Represents a new message response in a conversation
 */

data class NewMessageResponse(
    val conversationId: Int,
    val senderId: Int,
    val content: String,
    val sentAt: String
)

/**
 * Represents a detailed message response in a conversation
 */

data class MessageResponse(
    val id: Int,
    val conversationId: Int,
    val senderId: Int,
    val content: String,
    val sentAt: Instant,
    val isRead: Boolean
)

/**
 * Represents a request to create a new conversation
 */

data class SendMessageRequest(
    val conversationId: Int? = null,
    val itemId: Int,
    val content: String
)

/**
 * Represents a request to create a new conversation
 */

data class getConversationResponse(
    val otherParticipantName: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val messages: MutableList<MessageResponse>,
    val item: ItemCard
)
