package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.model.Conversation
import edu.ntnu.idatt2105.gr2.backend.model.Message
import edu.ntnu.idatt2105.gr2.backend.repository.ConversationRepository
import edu.ntnu.idatt2105.gr2.backend.repository.MessageRepository
import edu.ntnu.idatt2105.gr2.backend.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import org.slf4j.LoggerFactory

/**
 * Service class for managing conversations and messages.
 * Handles creating, sending, and retrieving conversations and messages.
 */

@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val itemService: ItemService,
    private val userContextService: UserContextService,
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(ConversationService::class.java)
    /**
     * Creates a new conversation and saves it to the database.
     *
     * @param request The request object containing the details of the conversation to be created.
     * @return A NewMessageResponse object containing the details of message sent in the created conversation.
     */

    fun createConversation(request: SendMessageRequest): NewMessageResponse {
        val buyerId = userContextService.getCurrentUserId()
        val conversation = conversationRepository.save(request.toModel(buyerId))

        messageRepository.save(
            Message(
                conversationId = conversation.id,
                senderId = buyerId,
                content = request.content,
                sentAt = Instant.now(),
            )
        )

        return NewMessageResponse(
            conversationId = conversation.id,
            senderId = buyerId,
            content = request.content,
            sentAt = Instant.now().toString()
        )
    }

    fun deleteConversation(id: Int) {
        conversationRepository.delete(id)
    }

    /**
     * Sends a message in an existing conversation and creates a new conversation if none exists.
     *
     * @param request The request object containing the details of the message to be sent.
     * @return A NewMessageResponse object containing the details of the sent message.
     */

    fun sendMessage(request: SendMessageRequest): NewMessageResponse {
        val senderId = userContextService.getCurrentUserId()
        val item = itemService.getItemById(request.itemId)
        val itemOwnerId = item.sellerId

        val conversationId = request.conversationId

        if (conversationId == null) {
            // No conversation ID provided - check if one already exists
            val existingConversation = conversationRepository.findConversationByParticipants(senderId, itemOwnerId, request.itemId)
            return if (existingConversation != null) {
                // Conversation exists, proceed to send message
                sendToConversation(existingConversation.id, senderId, request.content)
            } else {
                // No conversation exists, create a new one
                createConversation(request)
            }
        } else {
            // Conversation ID provided - validate it
            val conversation = conversationRepository.findConversationById(conversationId)
                ?: throw IllegalArgumentException("Conversation with id $conversationId not found")

            if (!hasAccessToConversation(conversation.id, senderId)) {
                throw IllegalArgumentException("User does not have access to this conversation")
            }

            return sendToConversation(conversation.id, senderId, request.content)
        }
    }

    /**
     * Sends a message to an existing conversation.
     *
     * @param conversationId The ID of the conversation to send the message to.
     * @param senderId The ID of the user sending the message.
     * @param content The content of the message to be sent.
     */

    private fun sendToConversation(conversationId: Int, senderId: Int, content: String): NewMessageResponse {
        val message = messageRepository.save(
            Message(
                id = -1,
                conversationId = conversationId,
                senderId = senderId,
                content = content,
                sentAt = Instant.now(),
            )
        )

        return NewMessageResponse(
            conversationId = message.conversationId,
            senderId = message.senderId,
            content = message.content,
            sentAt = message.sentAt.toString()
        )
    }

    /**
     * Retrieves a conversation by its ID.
     *
     * @param id The ID of the conversation to be retrieved.
     * @return A getConversationResponse object containing the conversation details.
     */

    fun getConversationById(id: Int): GetConversationResponse {
        val currentUserId = userContextService.getCurrentUserId()
        if (!hasAccessToConversation(id, currentUserId)) {
            throw IllegalArgumentException("User does not have access to this conversation")
        }

        val messages = messageRepository.getAllMessagesInConversation(id)
            .map { message ->
                MessageResponse(
                    id = message.id,
                    conversationId = message.conversationId,
                    senderId = message.senderId,
                    content = message.content,
                    sentAt = message.sentAt,
                    isRead = message.isRead
                )
            }
        val conversation = conversationRepository.findConversationById(id)
            ?: throw IllegalArgumentException("Conversation with id $id not found")

        val item = itemService.getItemById(conversation.itemId)

        val otherUserId = if (conversation.buyerId == currentUserId) {
            item.sellerId
        } else {
            conversation.buyerId
        }

        val otherParticipant = userRepository.findUserById(otherUserId)
        val otherParticipantName = otherParticipant?.name
            ?: throw IllegalArgumentException("User with id $otherUserId not found")

        logger.info("Other participant name: $otherParticipantName")

        return GetConversationResponse(
            otherParticipantName = otherParticipantName,
            createdAt = conversation.createdAt,
            updatedAt = conversation.updatedAt,
            messages = messages.toMutableList(),
            item = itemService.getItemCardById(conversation.itemId)
        )
    }

    /**
     * Retrieves all conversations for the current user.
     *
     * @return A ConversationsResponse object containing a list of conversation cards.
     */

    fun getAllConversationsForUser(): ConversationsResponse {
        val userId = userContextService.getCurrentUserId()
        val conversations = conversationRepository.findAllConversationsByUserId(userId)

        val conversationCards = conversations.map { conversation -> conversation.toResponse() }

        return ConversationsResponse(conversationCards)
    }

    /**
     * Checks if the user has access to a specific conversation.
     *
     * @param id The ID of the conversation to check.
     * @param userId The ID of the user to check access for.
     * @return True if the user has access to the conversation, false otherwise.
     */

    private fun hasAccessToConversation(id: Int, userId: Int): Boolean {
        val conversation = conversationRepository.findConversationById(id)
            ?: throw IllegalArgumentException("Access could not be granted because conversation with id $id not found")

        val sellerId = itemService.getItemById(conversation.itemId).sellerId

        return conversation.buyerId == userId || sellerId == userId
    }

    /**
     * Converts a Conversation object to a ConversationCardResponse object.
     *
     * @return A ConversationCardResponse object containing the conversation details.
     */


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

    /**
     * Converts a SendMessageRequest object to a Conversation object.
     *
     * @param buyerId The ID of the buyer.
     * @return A Conversation object containing the conversation details.
     */

    fun SendMessageRequest.toModel(buyerId: Int): Conversation {
        return Conversation(
            id = -1,
            itemId = this.itemId,
            buyerId = buyerId,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}
