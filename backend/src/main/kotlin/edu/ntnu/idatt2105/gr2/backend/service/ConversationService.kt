package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.model.Conversation
import edu.ntnu.idatt2105.gr2.backend.model.Message
import edu.ntnu.idatt2105.gr2.backend.repository.ConversationRepository
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import edu.ntnu.idatt2105.gr2.backend.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy
import java.time.Instant
import java.sql.Timestamp


@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val itemRepository: ItemRepository
) {
    fun createConversation(request: CreateConversationRequest, buyerId: Int): CreateConversationResponse {
        return conversationRepository.save(request.toModel(buyerId)).toResponse()
    }

    /*
    fun getConversationById(id: Int): Conversation? {
        return conversationRepository.findConversationById(id).toResponse()
    }
     */

    fun deleteConversation(id: Int) {
        conversationRepository.delete(id)
    }

    fun getAllConversationsForUser(userId: Int): ConversationsCardsResponse {
        val conversations = conversationRepository.findAllConversationsByUserId(userId)

        val conversationCards = conversations.map { convo ->
            val latestMessage = messageRepository.getLatestMessage(convo.id)
            val itemTitle = itemRepository.getItemById(convo.itemId)?.let { it.title } ?: "Unknown item name"

            ConversationCardResponse(
                id = convo.id,
                itemTitle = itemTitle,
                lastMessage = latestMessage?.content ?:"No messages",
                lastMessageTime = latestMessage?.sentAt?.toString() ?: "Unknown time",
                containsUnreadMessages = latestMessage?.isRead ?: false
            )
        }

        return ConversationsCardsResponse(conversations = conversationCards).toResponse()
    }



    fun hasAccessToConversation(id: Int, userId: Int): Boolean {
        val conversation = conversationRepository.findConversationById(id)
            ?: throw IllegalArgumentException("Access could not be granted because conversation with id $id not found")

        return conversation.buyerId == userId
    }

    fun sendMessage(conversationId: Int, senderId: Int, content: String): Message {
        if (!hasAccessToConversation(conversationId, senderId)) {
            throw IllegalArgumentException("User does not have access to this conversation")
        }

        val currentTimeStamp = Timestamp.from(Instant.now())
        val message = Message(
            id = -1,
            conversationId = conversationId,
            senderId = senderId,
            content = content,
            sentAt = currentTimeStamp
        )

        return messageRepository.save(message)
    }


    private fun CreateConversationResponse.toResponse(): CreateConversationResponse {
        return CreateConversationResponse(
            statusMessage = this.statusMessage
        )
    }

    fun CreateConversationRequest.toModel(
        buyerId: Int = -1): Conversation {
        return Conversation(
            id = -1,
            itemId = this.itemId,
            buyerId = buyerId,
            createdAt = Timestamp.from(Instant.now()),
            updatedAt = Timestamp.from(Instant.now())
        )
    }

    fun ConversationCardResponse.toResponse(): ConversationCardResponse {
        return ConversationCardResponse(
            id = this.id,
            itemTitle = this.itemTitle,
            lastMessage = this.lastMessage,
            lastMessageTime = this.lastMessageTime,
            containsUnreadMessages = this.containsUnreadMessages
        )
    }

    fun ConversationsCardsResponse.toResponse(): ConversationsCardsResponse {
        return ConversationsCardsResponse(
            conversations = this.conversations.map { it.toResponse() }
        )
    }
}
