package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.model.Conversation
import edu.ntnu.idatt2105.gr2.backend.service.ConversationService
import edu.ntnu.idatt2105.gr2.backend.service.UserContextService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/api/conversations")
class ConversationController(
    private val conversationService: ConversationService,
    private val userContextServer: UserContextService
) {

    @MessageMapping("/createConversation")
    @SendTo("/topic/conversations")
    fun createConversation(itemId: Int): Conversation {
        val buyerId = userContextServer.getCurrentUserId()
        return conversationService.createConversation(itemId, buyerId)
    }

    @MessageMapping("/getConversation")
    @SendTo("/topic/conversations")
    fun getConversationById(id: Int): Conversation? {
        return conversationService.getConversationById(id)
    }

    @MessageMapping("/getTest")
    @SendTo("/topic/test")
    fun test(): String {
        return "HELLLOOOOOOOOO:)"
    }

    @MessageMapping("/deleteConversation")
    @SendTo("/topic/conversations")
    fun deleteConversation(id: Int) {
        conversationService.deleteConversation(id)
    }
}