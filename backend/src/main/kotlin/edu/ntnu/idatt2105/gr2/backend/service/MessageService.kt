package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.Message
import edu.ntnu.idatt2105.gr2.backend.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.context.annotation.Lazy
import java.sql.Timestamp
import java.time.Instant

@Service
class MessageService(
    private val messageRepository: MessageRepository
) {
    fun createMessage(Id: Int, conversationId: Int, senderId: Int, content: String, sentAt: Timestamp): Message {

        val message = Message(
            id = Id,
            conversationId = conversationId,
            senderId = senderId,
            content = content,
            sentAt = Timestamp(System.currentTimeMillis())
        )
        return messageRepository.save(message)
    }




    fun getMessageById(id: Int): Message? {
        return messageRepository.findMessageById(id)
    }
}