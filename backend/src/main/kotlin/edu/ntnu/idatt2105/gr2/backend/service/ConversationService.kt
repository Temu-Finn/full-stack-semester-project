package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.Conversation
import edu.ntnu.idatt2105.gr2.backend.repository.ConversationRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.sql.Timestamp


@Service
class ConversationService(
    private val conversationRepository: ConversationRepository,
) {
    fun createConversation(itemId: Int, buyerId: Int): Conversation {
        val currentTimeStamp = Timestamp.from(Instant.now())
        val conversation = Conversation(
            itemId = itemId,
            buyerId = buyerId,
            createdAt = currentTimeStamp,
            updatedAt = currentTimeStamp
        )
        return conversationRepository.save(conversation)
    }

    fun getConversationById(id: Int): Conversation? {
        return conversationRepository.findConversationById(id)
    }

    fun deleteConversation(id: Int) {
        conversationRepository.delete(id)
    }
}
