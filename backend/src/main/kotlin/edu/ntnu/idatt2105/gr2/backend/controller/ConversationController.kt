package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.service.ConversationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping

/**
 * Controller for handling conversation-related requests. It includes endpoints for
 * getting all conversations, getting a conversation by ID, sending a message,
 * and deleting a conversation.
 *
 * @constructor                     Creates a new instance of ConversationController.
 * @param conversationService       The service for handling conversation-related
 *                                  operations.
 */

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversation", description = "Conversation management APIs")
class ConversationController(
    private val conversationService: ConversationService,
) {

    private val logger = LoggerFactory.getLogger(ConversationController::class.java)

    /**
     * Handles client requests to get all conversations. It delegates the request to the
     * service layer and returns response status and a list of conversations.
     *
     * @return   ResponseEntity<ConversationsResponse>      contains the list of conversations
     */

    @GetMapping("/getAll")
    @Operation(summary = "Get all active conversations for a user")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved conversations",
            ),
            ApiResponse(
                responseCode = "404",
                description = "No conversations found",
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
            ),
        ],
    )
    fun getAllConversations(): ResponseEntity<ConversationsResponse> {
        logger.info("fetching all conversations")
        val conversations = conversationService.getAllConversationsForUser()
        logger.info("Successfully fetched all conversations")
        return ResponseEntity.ok(conversations)
    }

    /**
     * Handles client requests to get a conversation by ID. It delegates the request to the
     * service layer and returns response status and conversation details from service layer
     * as requested.
     *
     * @param id   Int                                   The id of the conversation to retrieve.
     * @return     ResponseEntity<ConversationResponse>  contains the conversation details and status
     */

    @GetMapping("/{id}")
    @Operation(summary = "Get conversation by id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved conversation",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Conversation not found",
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
            ),
        ],
    )
    fun getConversation(
        @Parameter(description = "Conversation ID") @PathVariable id: Int,
    ): ResponseEntity<GetConversationResponse> {
        logger.info("Fetching conversation with id: $id")
        val conversation = conversationService.getConversationById(id)
        logger.info("Successfully fetched conversation with id: $id")
        return ResponseEntity.ok(conversation)
    }

    /**
     *  Handles client requests to delete a conversation by ID. It delegates the request to the
     *  service layer and returns response status
     *
     *  @param id   Int                                   The id of the conversation to delete.
     *  @return     ResponseEntity<Nothing>               contains the status of the deletion
     */

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a conversation by id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully deleted conversation",
            ),
            ApiResponse(
                responseCode = "404",
                description = "Delete failed: Conversation not found",
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
            ),
        ],
    )
    fun deleteConversation(
        @PathVariable id: Int
    ): ResponseEntity<Nothing> {
        conversationService.deleteConversation(id)
        logger.info("Successfully deleted conversation with id: $id")
        return ResponseEntity.ok().body(null)
    }

    /**
     * Handles client requests to send a message to another user. It delegates the request to the
     * service layer and returns response status and message/conversation details from service
     * layer as requested.
     *
     * @param request   SendMessageRequest                   The message details to send.
     * @return          ResponseEntity<NewMessageResponse>   contains the message, conversation
     *                                                       details and status
     */

    @PostMapping("/sendMessage")
    @Operation(summary = "Send a message to a conversation")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully sent message",
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request",
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
            ),
        ],
    )
    fun sendMessage(
        @Parameter(description = "Message request")
        @RequestBody request: SendMessageRequest,
    ): ResponseEntity<NewMessageResponse> {
        logger.info("Sending message to conversation")
        val response = conversationService.sendMessage(request)
        logger.info("Successfully sent message to conversation")
        return ResponseEntity.ok(response)
    }
}
