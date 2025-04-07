package edu.ntnu.idatt2105.gr2.backend.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class ItemResponse(
    val id: Long,
    val sellerId: Long,
    val categoryId: Long,
    val postalCode: String,
    val title: String,
    val description: String,
    val price: Double,
    val purchasePrice: Double?,
    val buyerId: Long?,
    val location: Pair<Double, Double>?,
    val allowVippsBuy: Boolean,
    val primaryImageId: Long?,
    val status: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class ItemsResponse(val items: List<ItemResponse>)

data class CreateItemRequest(
    @field:Positive(message = "Seller ID must be positive")
    val sellerId: Long,

    @field:Positive(message = "Category ID must be positive")
    val categoryId: Long,

    @field:NotBlank(message = "Postal code cannot be empty")
    val postalCode: String,

    @field:NotBlank(message = "Title cannot be empty")
    val title: String,

    @field:NotBlank(message = "Description cannot be empty")
    val description: String,

    @field:DecimalMin("0.0", inclusive = true, message = "Price must be zero or positive")
    val price: Double,

    val purchasePrice: Double? = null,
    val buyerId: Long? = null,

    val location: Pair<Double, Double>? = null, // Lat, Lng

    val allowVippsBuy: Boolean = false,

    val primaryImageId: Long? = null,

    @field:Pattern(
        regexp = "available|reserved|sold|archived",
        message = "Status must be one of: available, reserved, sold, archived"
    )
    val status: String = "available"
)