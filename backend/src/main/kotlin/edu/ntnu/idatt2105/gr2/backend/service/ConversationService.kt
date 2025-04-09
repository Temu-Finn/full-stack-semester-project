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
import java.time.LocalDateTime


@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val itemService: ItemService,
    private val userContextService: UserContextService,
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

    fun getAllConversationsForUser(): ConversationsCardsResponse {
        val userId = userContextService.getCurrentUserId()
        val conversations = conversationRepository.findAllConversationsByUserId(userId)

        val conversationCards = conversations.map { conversation -> conversation.toResponse() }

        return ConversationsCardsResponse(conversationCards)
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

        val message = Message(
            id = -1,
            conversationId = conversationId,
            senderId = senderId,
            content = content,
            sentAt = LocalDateTime.now(),
        )

        return messageRepository.save(message)
    }


    private fun CreateConversationResponse.toResponse(): CreateConversationResponse {
        return CreateConversationResponse(
            statusMessage = this.statusMessage
        )
    }

    fun CreateConversationRequest.toModel(buyerId: Int = -1): Conversation {
        return Conversation(
            id = -1,
            itemId = this.itemId,
            buyerId = buyerId,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    fun Conversation.toResponse(): ConversationCardResponse {
        val latestMessage = messageRepository.getLatestMessage(id)
        val item = itemService.getItemCardById(itemId)

        return ConversationCardResponse(
            id = id,
            lastMessage = latestMessage?.content,
            lastMessageTime = latestMessage?.sentAt,
            item = item,
        )
    }
}
