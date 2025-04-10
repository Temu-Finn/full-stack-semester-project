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
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.PositiveOrZero
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

/**
 * Controller for handling item-related requests. It includes endpoints for
 * getting item by ID, creating a new item, searching items, getting items of a user,
 * deleting an item, getting favorite items, and reserving an item.
 *
 * @constructor                     Creates a new instance of ItemController.
 * @param itemService               The service for handling item-related operations.
 */

@RestController
@RequestMapping("/api/item")
@Tag(name = "Item", description = "Item management APIs")
@Validated
class ItemController(
    private val itemService: ItemService,
) {

    private val logger = LoggerFactory.getLogger(ItemController::class.java)

    /**
     * Handles client requests to get an item by ID. It delegates the request to the
     * service layer and returns response data and item details.
     *
     * @param id   The ID of the item to retrieve.
     * @return     ResponseEntity<CompleteItem>      Contains the item details and a
     *                                               status code.
     */

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Returns an item by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Item retrieved successfully"),
            ApiResponse(responseCode = "404", description = "Item not found"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun getItemById(
        @Parameter(description = "Item ID", required = true) @PathVariable id: Int,
    ): ResponseEntity<CompleteItem> {
        val item = itemService.getItemById(id)
        return ResponseEntity.ok(item)
    }

    /**
     * Handles client requests to create a new item. It delegates the request to the
     * service layer and returns response status and the created item.
     *
     * @param itemRequest   CreateItemRequest                The item creation details.
     * @param images        List<MultipartFile>              The images to be saved.
     * @return              ResponseEntity<CompleteItem>     Contains the created item
     */

    @PostMapping
    @Operation(
        summary = "Create new item",
        description =
            "Creates a new item and returns it. This endpoint uses " +
                "form-data to support uploading images. The first image" +
                    " provided will be set as the primary image."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Item created"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun createItem(
        @RequestPart("item") @Valid itemRequest: CreateItemRequest,
        @RequestPart("image", required = false) images: List<MultipartFile> = emptyList(),
    ): ResponseEntity<CompleteItem> {
        logger.info("Creating new item: ${itemRequest.title}")
        val savedItem = itemService.createItem(itemRequest, images)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem)
    }

    /**
     * Handles client requests to get recommended items. It delegates the request to the
     * service layer and returns response status and recommended items from service layer .
     *
     * @return     ResponseEntity<RecommendedItemsResponse>  Contains the recommended items
     */

    @GetMapping("/recommended")
    @Operation(summary = "Recommended items", description = "Utilizes user data to recommend items")
    fun recommendedItems(): ResponseEntity<RecommendedItemsResponse> {
        logger.info("Fetching recommended items")
        val items = itemService.getRecommendedItems()
        logger.info("Successfully fetched recommended items")
        return ResponseEntity.ok(RecommendedItemsResponse(items))
    }

    /**
     * Handles client requests to search for items. It delegates the request to the
     * service layer and returns response status and search results from service layer.
     *
     * @param searchText     String?           The text to search for in item titles and descriptions.
     * @param categoryId     Int?              The ID of the category to filter items.
     * @param county         String?           The county to filter items.
     * @param municipality   String?           The municipality to filter items.
     * @param city           String?           The city to filter items.
     * @param latitude       Double?           The latitude for distance search (-90 to 90).
     * @param longitude      Double?           The longitude for distance search (-180 to 180).
     * @param maxDistanceKm  Double?           The maximum distance in kilometers (must be >=0).
     * @param pageable       Pageable          The pagination information.
     *
     * @return               ResponseEntity<SearchResponse>  Contains the search results and
     *                                                       a status code.
     */

    @GetMapping("/search")
    @Operation(summary = "Search items with pagination",
        description = "Search items with various filters, returns results page by page.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
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
        @Parameter(description = "Latitude for distance search (-90 to 90)")
        @RequestParam(required = false)
        @Min(-90)
        @Max(90) latitude: Double?,
        @Parameter(description = "Longitude for distance search (-180 to 180)")
        @RequestParam(required = false)
        @Min(-180)
        @Max(180) longitude: Double?,
        @Parameter(description = "Maximum distance in kilometers (must be zero or positive)")
        @RequestParam(required = false)
        @PositiveOrZero maxDistanceKm: Double?,
        @Parameter(hidden = true) @PageableDefault(size = 20, sort = ["updated_at"]) pageable: Pageable,
    ): ResponseEntity<SearchResponse> {
        logger.info(
            "Searching items with text: $searchText, category: $categoryId, county: $county," +
                    " municipality: $municipality, city: $city, lat: $latitude, lon: $longitude," +
                    " distKm: $maxDistanceKm, pageable: $pageable",
        )
        val searchRequest =
            SearchRequest(
                searchText = searchText,
                categoryId = categoryId,
                county = county,
                municipality = municipality,
                city = city,
                latitude = latitude,
                longitude = longitude,
                maxDistanceKm = maxDistanceKm,
            )
        val searchResult = itemService.searchItems(searchRequest, pageable)
        return ResponseEntity.ok(searchResult)
    }

    /**
     * Handles client requests to get items of a specific user. It delegates the request to the
     * service layer and returns response status and items from service layer.
     *
     * @param userId   Int                             The ID of the user whose items to retrieve.
     * @return         ResponseEntity<List<ItemCard>>  Contains the list of items and a status code.
     */

    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get items of user",
        description = "Returns all items of a specific user. If it is current user," +
                " it returns all items, including archived, sold, and reserved",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Items retrieved successfully"),
            ApiResponse(responseCode = "404", description = "User not found"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun getItemsOfUser(
        @Parameter(description = "User ID to filter items")
        @PathVariable userId: Int,
    ): ResponseEntity<List<ItemCard>> {
        logger.info("Fetching items for user ID: $userId")
        val items = itemService.getItemsOfUser(userId)
        return ResponseEntity.ok(items)
    }

    /**
     * Handles client requests to delete an item by ID. It delegates the request to the
     * service layer and returns response status.
     *
     * @param id   Int   The ID of the item to delete.
     * @return     ResponseEntity<Void>  Contains a status code.
     */

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item by ID", description = "Deletes an item by its ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Item deleted successfully"),
            ApiResponse(responseCode = "403", description = "User is not the owner"),
            ApiResponse(responseCode = "404", description = "Item not found"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun deleteItem(
        @Parameter(description = "Item ID to delete", required = true)
        @PathVariable id: Int,
    ): ResponseEntity<Void> {
        logger.info("Request to delete item ID: $id")
        val deleted = itemService.deleteItem(id)
        if (!deleted) {
            throw ItemNotFoundException("Item with ID $id not found")
        }
        logger.info("Item with ID $id deleted")
        return ResponseEntity.noContent().build()
    }

    /**
     * Handles client requests to get favorite items. It delegates the request to the
     * service layer and returns response status and favorite items from service layer.
     *
     * @param userId   Int                             Users favorited items.
     * @return         ResponseEntity<List<ItemCard>>  Contains the list of favorite
     *                                                 items and a status code.
     */

    @GetMapping("/favorites")
    @Operation(summary = "Get favorite items", description = "Retrieves all favorited items for the current user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Favorites retrieved"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
        ],
    )
    fun getFavoriteItems(): ResponseEntity<List<ItemCard>> {
        logger.info("Request to get current user's favorite items")
        val favorites = itemService.getFavoriteItemsOfCurrentUser()
        return ResponseEntity.ok(favorites)
    }

    /**
     * Handles client requests to reserve an item. It delegates the request to the
     * service layer and returns response status and the reserved item from the service layer.
     *
     * @param itemId   Int                           The ID of the item to reserve.
     * @return         ResponseEntity<CompleteItem>  Contains the reserved item and a status code.
     */

    @PostMapping("/reserve/{itemId}")
    @Operation(summary = "Reserve an item", description = "Reserves an item for the current user")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Item reserved successfully"),
            ApiResponse(responseCode = "404", description = "Item not found"),
            ApiResponse(responseCode = "500", description = "Internal server error"),
        ],
    )
    fun reserveItem(
        @Parameter(description = "Item ID to reserve", required = true)
        @PathVariable itemId: Int,
    ): ResponseEntity<CompleteItem> {
        logger.info("Request to reserve item")
        val reservedItem = itemService.reserveItem(itemId)
        logger.info("Item reserved successfully")
        return ResponseEntity.ok().body(reservedItem)
    }
}
