package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.ConversationCardResponse
import edu.ntnu.idatt2105.gr2.backend.dto.ConversationsCardsResponse
import edu.ntnu.idatt2105.gr2.backend.dto.CreateConversationRequest
import edu.ntnu.idatt2105.gr2.backend.dto.CreateConversationResponse
import edu.ntnu.idatt2105.gr2.backend.model.Conversation
import edu.ntnu.idatt2105.gr2.backend.repository.ItemRepository
import edu.ntnu.idatt2105.gr2.backend.service.ConversationService
import edu.ntnu.idatt2105.gr2.backend.service.UserContextService
import org.slf4j.LoggerFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversation", description = "Conversation management APIs")
class ConversationController(
    private val conversationService: ConversationService,
    private val userContextService: UserContextService,
    private val itemRepository: ItemRepository,
) {

    private val logger = LoggerFactory.getLogger(ConversationController::class.java)

    @PostMapping("/create")
    @Operation(summary = "Create a new conversation")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully created conversation"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    fun createConversation(request: CreateConversationRequest):
            CreateConversationResponse {
        val buyerId = userContextService.getCurrentUserId()
        return conversationService.createConversation(request, buyerId)
    }

    //sort by latest message
    @GetMapping("/getAll")
    @Operation(summary = "Get all active conversations for a user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved conversations"
            ),
            ApiResponse(
                responseCode = "404",
                description = "No conversations found"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    fun getAllConversations(): ConversationsCardsResponse {
        logger.info("fetching all conversations")
        val currentUserId = userContextService.getCurrentUserId()
        val conversations = conversationService.getAllConversationsForUser(currentUserId)
        logger.info("Successfully fetched all conversations")
        return conversations
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

    /*
    @MessageMapping("/updateConversation")
    @SendTo("/topic/conversations/{conversationId}")
    fun updateConversation(conversationId: Int, buyerId: Int) {
        val userId = userContextService.getCurrentUserId()
        conversationService.sendMessage(conversationId, userId, "test")
    }
     */
}