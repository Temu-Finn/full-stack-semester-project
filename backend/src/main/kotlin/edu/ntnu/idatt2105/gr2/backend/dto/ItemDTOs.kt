package edu.ntnu.idatt2105.gr2.backend.dto

import edu.ntnu.idatt2105.gr2.backend.model.ItemStatus
import jakarta.validation.constraints.*
import java.time.LocalDateTime

/**
 * Basic item information used in list views and search results
 */
data class ItemCard(
    val id: Int,
    val title: String,
    val price: Double,
    val municipality: String,
    val image: ImageResponse?,
    val location: Location?,
    val status: String,
    val updatedAt: LocalDateTime
)

/**
 * Detailed item information used in product detail pages
 */
data class CompleteItem(
    val id: Int,
    val sellerId: Int,
    val category: CategoryResponse,
    val postalCode: String,
    val title: String,
    val description: String,
    val price: Double,
    val purchasePrice: Double?,
    val buyerId: Int?,
    val location: Location?,
    val allowVippsBuy: Boolean,
    val primaryImageId: Int?,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val municipality: String,
    val images: List<ImageResponse>
)


data class RecommendedItemsResponse(
    val items: List<ItemCard>
)

data class CreateItemRequest(
    @field:Positive(message = "Category ID must be positive")
    val categoryId: Int,

    @field:NotBlank(message = "Postal code cannot be empty")
    val postalCode: String,

    @field:NotBlank(message = "Title cannot be empty")
    val title: String,

    @field:NotBlank(message = "Description cannot be empty")
    val description: String,

    @field:DecimalMin("0.0", inclusive = true, message = "Price must be zero or positive")
    val price: Double,

    val location: Location? = null,

    @field:NotNull(message = "Allow Vipps Buy must be true or false")
    val allowVippsBuy: Boolean = false,
)

data class Location(
    @field:DecimalMin("-90.0") @field:DecimalMax("90.0")
    val latitude: Double,
    @field:DecimalMin("-180.0") @field:DecimalMax("180.0")
    val longitude: Double
)
