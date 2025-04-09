package edu.ntnu.idatt2105.gr2.backend.dto

import java.time.Instant

data class ConversationCardResponse(
    val id: Int,
    val lastMessage: String?,
    val lastMessageTime: Instant?,
    val item: ItemCard
)

data class ConversationsResponse(
    val conversations: List<ConversationCardResponse>
)

data class NewMessageResponse(
    val conversationId: Int,
    val senderId: Int,
    val content: String,
    val sentAt: String
)

data class Message(
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

data class getConversationRequest(
    val id: Int,
)

data class getConversationResponse(
    val otherParticipantName: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val messages: MutableList<Message>,
    val item: ItemCard
)