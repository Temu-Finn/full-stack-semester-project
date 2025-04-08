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
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/item")
@Tag(name = "Item", description = "Item management APIs")
class ItemController (
    private val itemService: ItemService,
) {
    private val logger = LoggerFactory.getLogger(ItemController::class.java)

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Returns an item by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Item retrieved successfully"),
            ApiResponse(responseCode = "404", description = "Item not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun getItemById(@Parameter(description = "Item ID", required = true) @PathVariable id: Int): ResponseEntity<CompleteItem> {
        val item = itemService.getItemById(id)
        return ResponseEntity.ok(item)
    }

    @PostMapping
    @Operation(summary = "Create new item", description = "Creates a new item and returns it. This endpoint uses" +
            "form-data to support uploading images. The first image provided will be set as the primary image.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Item created"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun createItem(
        @RequestPart("item") @Valid itemRequest: CreateItemRequest,
        @RequestPart("image") images: List<MultipartFile>,
    ): ResponseEntity<CompleteItem> {
        val request = itemRequest.copy(images = images.map { CreateImageRequest(imageFile = it) })
        logger.info("Creating new item: ${request.title}")
        val savedItem = itemService.createItem(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem)
    }

    @GetMapping("/recommended")
    @Operation(summary = "Recommended items", description = "Utilizes user data to recommend items")
    fun recommendedItems(): ResponseEntity<RecommendedItemsResponse> {
        logger.info("Fetching recommended items")
        val items = itemService.getRecommendedItems()
        logger.info("Successfully fetched recommended items")
        return ResponseEntity.ok(RecommendedItemsResponse(items))
    }

    @GetMapping("/search")
    @Operation(summary = "Search items", description = "Search items with various filters")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun searchItems(
        @Parameter(description = "Search text to filter items")
        @RequestParam(required = false) searchText: String?,
        @Parameter(description = "Category ID to filter items")
        @RequestParam(required = false) categoryId: Int?,
        @Parameter(description = "County to filter items")
        @RequestParam(required = false) county: String?,
        @Parameter(description = "Municipality to filter items")
        @RequestParam(required = false) municipality: String?,
        @Parameter(description = "City to filter items")
        @RequestParam(required = false) city: String?,
        @Parameter(description = "Latitude for distance search")
        @RequestParam(required = false) latitude: Double?,
        @Parameter(description = "Longitude for distance search")
        @RequestParam(required = false) longitude: Double?,
        @Parameter(description = "Maximum distance in kilometers")
        @RequestParam(required = false) maxDistance: Double?
        
    ): ResponseEntity<SearchResponse> {
        logger.info("Searching items with text: $searchText, category: $categoryId, county: $county, municipality: $municipality, city: $city, lat: $latitude, lon: $longitude, dist: $maxDistance")
        val items = itemService.searchItems(SearchItemRequest(
            searchText = searchText, 
            categoryId = categoryId,
            county = county,
            municipality = municipality,
            city = city,
            latitude = latitude,
            longitude = longitude,
            maxDistance = maxDistance
        ))
        return ResponseEntity.ok(SearchResponse(items))
    }

    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get items of user",
        description = "Returns all items of a specific user. If it is current user, it returns all items, including archived, sold, and reserved"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
            ApiResponse(responseCode = "404", description = "User not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    fun getItemsOfUser(
        @Parameter(description = "User ID to filter items")
        @PathVariable userId: Int
    ): ResponseEntity<List<ItemCard>> {
        logger.info("Fetching items for user ID: $userId")
        val items = itemService.getItemsOfUser(userId)
        return ResponseEntity.ok(items)
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
