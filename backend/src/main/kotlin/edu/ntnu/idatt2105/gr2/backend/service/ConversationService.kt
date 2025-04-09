package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.model.Conversation
import edu.ntnu.idatt2105.gr2.backend.model.Message
import edu.ntnu.idatt2105.gr2.backend.repository.ConversationRepository
import edu.ntnu.idatt2105.gr2.backend.repository.MessageRepository
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val itemService: ItemService,
    private val userContextService: UserContextService,
    private val userRepository: UserRepository
) {
    fun createConversation(request: CreateConversationRequest): CreateConversationResponse {
        val buyerId = userContextService.getCurrentUserId()
        return conversationRepository.save(request.toModel(buyerId)).toResponse()
    }

    fun deleteConversation(id: Int) {
        conversationRepository.delete(id)
    }

    fun getConversationById(id: Int): getConversationResponse {
        val currentUserId = userContextService.getCurrentUserId()
        if (!hasAccessToConversation(id, currentUserId)) {
            throw IllegalArgumentException("User does not have access to this conversation")
        }
        val conversation = conversationRepository.findConversationById(id)
            ?: throw IllegalArgumentException("Conversation with id $id not found")


        val messages = messageRepository.getAllMessagesInConversation(id)

        val sellerId = itemService.getItemById(conversation.itemId).sellerId

        // is needed to set correct name
        val isItemOwner = currentUserId == sellerId
        val otherParticipantName: String

        if(isItemOwner) {
            otherParticipantName = userRepository.findUserById(conversation.buyerId)?.name
                ?: throw IllegalArgumentException("Buyer with id ${conversation.buyerId} not found")
        } else {
            otherParticipantName = userRepository.findUserById(sellerId)?.name
                ?: throw IllegalArgumentException("Seller with id $sellerId not found")
        }

        return getConversationResponse(
            otherParticipantName=  otherParticipantName,
            createdAt = conversation.createdAt,
            updatedAt = conversation.updatedAt,
            messages = messages,
            item = itemService.getItemCardById(conversation.itemId)
        )
    }

    fun getAllConversationsForUser(): ConversationsResponse {
        val userId = userContextService.getCurrentUserId()
        val conversations = conversationRepository.findAllConversationsByUserId(userId)

        val conversationCards = conversations.map { conversation -> conversation.toResponse() }

        return ConversationsResponse(conversationCards)
    }


    // WHAT IF THE USER IS NOT THE BUYER?
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
