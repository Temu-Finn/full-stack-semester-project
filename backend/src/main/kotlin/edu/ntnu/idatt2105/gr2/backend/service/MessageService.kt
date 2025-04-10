package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.Message
import edu.ntnu.idatt2105.gr2.backend.repository.MessageRepository
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

/**
 * Service class for managing messages.
 * Handles creating and retrieving messages.
 */
@Service
class MessageService(
    private val messageRepository: MessageRepository
) {
    fun createMessage(id: Int, conversationId: Int, senderId: Int, content: String, sentAt: Timestamp): Message {

        val message = Message(
            id = id,
            conversationId = conversationId,
            senderId = senderId,
            content = content,
            sentAt = Instant.now()
        )
        return messageRepository.save(message)
    }




    fun getMessageById(id: Int): Message? {
        return messageRepository.findMessageById(id)
    }
}