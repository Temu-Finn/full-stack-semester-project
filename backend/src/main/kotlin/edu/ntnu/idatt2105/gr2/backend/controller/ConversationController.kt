package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.service.ConversationService
import org.slf4j.LoggerFactory
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversation", description = "Conversation management APIs")
class ConversationController(
    private val conversationService: ConversationService,
) {

    private val logger = LoggerFactory.getLogger(ConversationController::class.java)

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
    fun getAllConversations(): ResponseEntity<ConversationsResponse> {
        logger.info("fetching all conversations")
        val conversations = conversationService.getAllConversationsForUser()
        logger.info("Successfully fetched all conversations")
        return ResponseEntity.ok(conversations)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get conversation by id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved conversation"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Conversation not found"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    fun getConversation(
        @Parameter(description = "Conversation ID") @PathVariable id: Int
    ): ResponseEntity<GetConversationResponse> {
        logger.info("Fetching conversation with id: $id")
        val conversation = conversationService.getConversationById(id)
        logger.info("Successfully fetched conversation with id: $id")
        return ResponseEntity.ok(conversation)
    }

    @MessageMapping("/deleteConversation")
    @SendTo("/topic/conversations")
    fun deleteConversation(id: Int) {
        conversationService.deleteConversation(id)
    }

    @PostMapping("/sendMessage")
    @Operation(summary = "Send a message to a conversation")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully sent message"
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
    fun sendMessage(
        @Parameter(description = "Message request")
        @RequestBody request: SendMessageRequest
    ): ResponseEntity<NewMessageResponse> {
        logger.info("Sending message to conversation")
        logger.info("itemid: ${request.itemId}")
        val response = conversationService.sendMessage(request)
        logger.info("Successfully sent message to conversation")
        return ResponseEntity.ok(response)
    }
}