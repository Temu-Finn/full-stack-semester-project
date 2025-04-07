package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.model.Item
import edu.ntnu.idatt2105.gr2.backend.service.ItemService
import edu.ntnu.idatt2105.gr2.backend.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/items")
@Tag(name = "Item", description = "Item management APIs")
class ItemController(private val itemService: ItemService) {
    private val logger = LoggerFactory.getLogger(AuthenticationController::class.java)

    @PostMapping
    @Operation(summary = "Create new item", description = "Creates a new item and returns it")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Item created"),
            ApiResponse(responseCode = "400", description = "Item creation failed"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun createItem(
        @Parameter(description = "Item data to create", required = true)
        @RequestBody @Valid request: CreateItemRequest
    ): ResponseEntity<ItemResponse> {
        logger.info("Creating new item: ${request.title}")
        val newItem = Item(
            sellerId = request.sellerId,
            categoryId = request.categoryId,
            postalCode = request.postalCode,
            title = request.title,
            description = request.description,
            price = request.price,
            purchasePrice = request.purchasePrice,
            buyerId = request.buyerId,
            location = request.location,
            allowVippsBuy = request.allowVippsBuy,
            primaryImageId = request.primaryImageId,
            status = request.status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val saved = itemService.createItem(newItem)

        // Manually map model to response DTO
        val response = ItemResponse(
            id = saved.id,
            sellerId = saved.sellerId,
            categoryId = saved.categoryId,
            postalCode = saved.postalCode,
            title = saved.title,
            description = saved.description,
            price = saved.price,
            purchasePrice = saved.purchasePrice,
            buyerId = saved.buyerId,
            location = saved.location,
            allowVippsBuy = saved.allowVippsBuy,
            primaryImageId = saved.primaryImageId,
            status = saved.status,
            createdAt = saved.createdAt,
            updatedAt = saved.updatedAt
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}