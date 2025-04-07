package edu.ntnu.idatt2105.gr2.backend.dto

import edu.ntnu.idatt2105.gr2.backend.model.ItemStatus
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class ItemCard(
    val id: Int,
    val title: String,
    val price: Double,
    val municipality: String,
    val imageBase64: String?,
    val location: Location?,
    val status: ItemStatus,
    val updatedAt: LocalDateTime?
)

data class RecommendedItemsResponse(
    val items: List<ItemCard>
)

data class SearchResult(
    val items: List<ItemCard>
)

data class ItemResponse(
    val id: Int,
    val sellerId: Int,
    val categoryId: Int,
    val postalCode: String,
    val title: String,
    val description: String,
    val price: Double,
    val purchasePrice: Double?,
    val buyerId: Int?,
    val location: Location?,
    val allowVippsBuy: Boolean,
    val primaryImageId: Int?,
    val status: ItemStatus,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class ItemsResponse(val items: List<ItemResponse>)

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

    val purchasePrice: Double? = null,
    val buyerId: Int? = null,
    val location: Location? = null,
    val allowVippsBuy: Boolean = false,
    val primaryImageId: Int? = null,
    val status: ItemStatus = ItemStatus.Available
)

data class Location(
    @field:DecimalMin("-90.0") @field:DecimalMax("90.0")
    val latitude: Double,
    @field:DecimalMin("-180.0") @field:DecimalMax("180.0")
    val longitude: Double
)