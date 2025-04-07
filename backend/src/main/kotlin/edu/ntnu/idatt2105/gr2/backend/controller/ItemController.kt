package edu.ntnu.idatt2105.gr2.backend.controller

import edu.ntnu.idatt2105.gr2.backend.dto.*
import edu.ntnu.idatt2105.gr2.backend.exception.ItemNotFoundException
import edu.ntnu.idatt2105.gr2.backend.service.ItemService
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/item")
@Tag(name = "Item", description = "Item management APIs")
class ItemController (
    private val itemService: ItemService,
) {
    private val logger = LoggerFactory.getLogger(ItemController::class.java)

    @PostMapping
    @Operation(summary = "Create new item", description = "Creates a new item and returns it")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Item created"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun createItem(
        @Parameter(description = "Item data to create", required = true)
        @RequestBody @Valid request: CreateItemRequest
    ): ResponseEntity<ItemResponse> {
        logger.info("Creating new item: ${request.title}")
        val savedItem = itemService.createItem(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem.toResponse())
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get items by category ID", description = "Returns all items in a specific category")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
            ApiResponse(responseCode = "404", description = "No items found for category")
        ]
    )
    fun getItemsByCategoryId(
        @Parameter(description = "Category ID", required = true)
        @PathVariable categoryId: Int
    ): ResponseEntity<ItemsResponse> {
        logger.info("Fetching items for category ID: $categoryId")
        val items = itemService.getItemsByCategoryId(categoryId).map { it.toResponse() }
        return ResponseEntity.ok(ItemsResponse(items))
    }

    @GetMapping("/recommended")
    @Operation(summary = "Recommended items", description = "Utilizes user data to recommend items")
    fun recommendedItems(): ResponseEntity<RecommendedItemsResponse> {
        logger.info("Fetching recommended items")
        val items = itemService.getRecommendedItems()
        logger.info("Successfully fetched recommended items")
        return ResponseEntity.ok(RecommendedItemsResponse(items))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item by ID", description = "Deletes an item by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Item deleted successfully"),
            ApiResponse(responseCode = "403", description = "User is not the owner"),
            ApiResponse(responseCode = "404", description = "Item not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun deleteItem(
        @Parameter(description = "Item ID to delete", required = true)
        @PathVariable id: Int
    ): ResponseEntity<Void> {
        logger.info("Request to delete item ID: $id")
        val deleted = itemService.deleteItem(id)
        if (!deleted) {
            throw ItemNotFoundException("Item with ID $id not found")
        }
        logger.info("Item with ID $id deleted")
        return ResponseEntity.noContent().build()
    }
}
